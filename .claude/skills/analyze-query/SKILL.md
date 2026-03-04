---
name: analyze-query
description:
  대상이 되는 코드 범위를 탐색하고, Spring @Transactional, JPA, QueryDSL 기반의 코드에 대해 트랜잭션 범위, 영속성 컨텍스트, 쿼리 실행 시점 관점에서 분석한다.

  특히 다음을 중점적으로 점검한다.
  - 트랜잭션이 불필요하게 크게 잡혀 있지는 않은지
  - 조회/쓰기 로직이 하나의 트랜잭션에 혼합되어 있지는 않은지
  - JPA의 지연 로딩, flush 타이밍, 변경 감지로 인해
    의도치 않은 쿼리 또는 락이 발생할 가능성은 없는지

  단순한 정답 제시가 아니라, 현재 구조의 의도와 trade-off를 드러내고 개선 가능 지점을 선택적으로 판단할 수 있도록 돕는다.
---

### Analysis Scope
이 스킬은 아래 대상에 대해 분석한다.
- @Transactional 이 선언된 클래스 / 메서드
- Service / Facade / Application Layer 코드
- JPA Entity, Repository, QueryDSL 사용 코드
- 하나의 유즈케이스(요청 흐름) 단위

> 컨트롤러 → Facade → 서비스 → 레포지토리 전체 흐름을 기준으로 분석하며, 특정 메서드만 떼어내어 판단하지 않는다.

### Project Context
본 프로젝트의 아키텍처 특성을 전제로 분석한다.

#### 레이어 구조
```
Controller → Facade (@Transactional 경계) → Domain Service (@Transactional) → Repository
```
- **Facade**: 트랜잭션 시작점. 여러 도메인 서비스를 조합하여 유즈케이스를 구현한다.
- **Domain Service**: Facade 트랜잭션에 참여(REQUIRED). 단일 도메인 내 비즈니스 규칙을 처리한다.
- **Repository**: 3-Tier (도메인 인터페이스 → 구현체 → JPA Repository)

#### 엔티티 설계 방식
- **JPA 관계 매핑(@OneToMany, @ManyToOne) 미사용** — ID 참조 방식
- Facade에서 관련 도메인을 명시적으로 조합 (예: Product + Brand + LikeCount)
- 변경 감지(dirty checking)를 통한 암묵적 UPDATE 사용 (예: `product.decreaseStock()`)
- BaseEntity 상속: id, createdAt, updatedAt, deletedAt 자동 관리, `guard()` 훅

#### 주요 도메인 흐름 예시
```
OrderFacade.createOrder()
  ├─ MemberService.getMyInfo()        // 인증 (조회)
  ├─ ProductService.getById()         // 상품 검증 (조회)
  ├─ ProductService.decreaseStock()   // 재고 차감 (쓰기 - dirty checking)
  ├─ BrandService.getById()           // 브랜드 조회 (조회)
  ├─ OrderService.createOrder()       // 주문 생성 (쓰기)
  ├─ PointService.use()               // 포인트 차감 (쓰기 - dirty checking)
  └─ OrderService.getOrderItems()     // 주문 항목 조회 (조회)
```

### Analysis Checklist

#### 1. Transaction Boundary 분석
다음을 순서대로 확인한다.
- 트랜잭션 시작 지점은 어디인가?
  - Facade / Service / 그 외 계층?
- 트랜잭션이 실제로 필요한 작업은 무엇인가?
  - 상태 변경 (쓰기): save(), dirty checking에 의한 UPDATE
  - 단순 조회: readOnly로 분리 가능한가?
- 트랜잭션 내부에서 수행되는 작업을 나열한다
  - 조회 전용 호출 (검증 목적)
  - 상태 변경 호출
  - 외부 API / 이벤트 발행
  - 반복문 기반 처리

**출력 형식**
```
- 현재 트랜잭션 범위:
XxxFacade.method()
  ├─ [조회] 유저 검증
  ├─ [조회] 상품 조회
  ├─ [쓰기] 재고 차감 (dirty checking)
  ├─ [쓰기] 주문 생성 (save)
  └─ [쓰기] 포인트 차감 (dirty checking)

- 트랜잭션이 필요한 핵심 작업:
  - 재고 차감, 주문 생성, 포인트 차감 (원자성 보장 필요)

- 트랜잭션 밖으로 분리 가능한 작업:
  - 유저 검증 (사전 검증)
  - 응답용 조회 (사후 조회)
```

#### 2. 불필요하게 큰 트랜잭션 식별
아래 패턴이 존재하는지 점검한다.
- [ ] Controller에서 @Transactional 사용
- [ ] 읽기 전용 로직이 쓰기 트랜잭션에 포함됨
  - 예: 검증 목적 조회가 @Transactional 안에서 실행
  - 예: 응답 조립용 조회가 쓰기 트랜잭션 말미에 포함
- [ ] 외부 시스템 호출이 트랜잭션 내부에 포함됨
- [ ] 트랜잭션 내부에서 대량 조회 / 복잡한 QueryDSL 실행
- [ ] 반복문 내부에서 개별 save() 호출 (batch insert 미활용)
- [ ] 상태 변경 완료 후에도 트랜잭션이 불필요하게 유지됨

**문제 후보 예시**
```
- 주문 생성 트랜잭션 내에서 응답용 OrderItems 재조회가 포함됨
  → 트랜잭션 종료 후 별도 readOnly 조회로 분리 가능
- for문으로 OrderItem을 건건이 saveItem() 호출
  → saveAll() 또는 batch insert 고려
```

#### 3. JPA / 영속성 컨텍스트 관점 분석
다음을 중심으로 분석한다.

**변경 감지 (Dirty Checking)**
- [ ] 조회한 Entity가 의도치 않게 변경 감지 대상이 되는가?
- [ ] `product.decreaseStock()` 같은 도메인 메서드 호출 후 명시적 save() 없이 UPDATE 발생 — 이 동작이 의도된 것인가?
- [ ] readOnly가 아닌 트랜잭션에서 조회만 하는 Entity가 있는가?

**Flush 타이밍**
- [ ] JPQL/QueryDSL 실행 전 auto-flush로 인한 예상치 못한 INSERT/UPDATE 가능성
- [ ] 트랜잭션 종료 시점의 flush로 인해 예외가 늦게 발생할 가능성

**영속성 컨텍스트 범위**
- [ ] 같은 Entity를 트랜잭션 내에서 중복 조회하는가? (1차 캐시 활용 여부)
- [ ] 대량 Entity 로딩으로 영속성 컨텍스트 메모리 부담 가능성

**본 프로젝트 특이사항**
- JPA 관계 매핑 미사용 → Lazy Loading / N+1 위험 낮음
- ID 참조 방식 → 각 도메인 독립적 조회, 영속성 전파 없음
- `@Transactional(readOnly = true)` 적용 시 flush 모드 MANUAL → 변경 감지 비활성화

#### 4. Facade-Service 간 트랜잭션 전파 분석
본 프로젝트는 Facade와 Service 양쪽에 `@Transactional`이 선언되어 있다.
- [ ] Service의 `@Transactional`이 Facade 트랜잭션에 참여(REQUIRED)하는 것이 의도된 동작인가?
- [ ] Service 단독 호출 시에도 트랜잭션이 보장되는가?
- [ ] readOnly Facade에서 호출된 Service의 쓰기 메서드가 있는가? (readOnly 전파 주의)
- [ ] 중첩 트랜잭션으로 인한 예상치 못한 롤백 범위 확대 가능성

#### 5. Improvement Proposal (선택적 제안)
개선안은 강제하지 않고 선택지로 제시한다. 현재 구조의 의도를 존중하되, trade-off를 명확히 한다.

**가능한 개선 방향**
- 트랜잭션 분리
  - 사전 검증(조회) → 핵심 쓰기 → 사후 조회 분리
  - Facade에서 orchestration, Service는 최소 트랜잭션
- `@Transactional(readOnly = true)` 누락 점검
- 반복문 내 개별 save → batch 처리
- 외부 호출 / 이벤트 발행을 트랜잭션 외부로 이동
- DTO Projection 도입 (변경 감지 불필요한 조회)

**개선안 작성 형식**
```
[개선안 N] 제목
- 현재: (현재 동작 설명)
- 제안: (개선 방향)
- 이유: (왜 개선이 필요한지)
- trade-off: (개선 시 발생할 수 있는 부작용 또는 고려사항)
- 판단: 개발자가 결정할 사항 명시
```

### 분석 시 주의사항
- 단순 패턴 매칭으로 문제를 지적하지 않는다. 흐름 전체를 보고 판단한다.
- "이렇게 하면 안 된다"가 아니라 "이렇게 하면 이런 trade-off가 있다"로 제시한다.
- 현재 규모와 요구사항에서 과도한 최적화를 권하지 않는다.
- 개선이 필요 없는 경우 "현재 구조가 적절하다"고 명시한다.
