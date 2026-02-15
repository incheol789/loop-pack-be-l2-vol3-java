# 요구사항 명세

> 유저 시나리오 기반 기능 정의 및 요구사항 명세

---

## 1. 서비스 개요

- 감성 이커머스 플랫폼

### 서비스 흐름
- 사용자가 **회원가입** 후 로그인한다.
- 다양한 브랜드의 **상품을 탐색**하고, 마음에 드는 상품에 **좋아요**를 누른다. 
- 원하는 상품을 골라 **한 번에 주문**하고, 추후 **결제**를 진행한다. 
- 유저의 행동(좋아요, 주문)은 기록되어 **랭킹/추천** 등으로 확장될 수 있다.

### 주요 기능 영역
- **구매자**: 상품 탐색(브랜드별 필터, 정렬, 페이징), 좋아요 등록/취소, 다건 상품 주문, 주문 이력 조회
- **어드민**: 브랜드 CRUD, 상품 CRUD(재고 관리 포함), 전체 주문 현황 조회

### 인증 방식
- 대고객 API (`/api/v1`): `X-Loopers-LoginId` + `X-Loopers-LoginPw` 헤더로 유저 식별. 인증/인가 자체는 구현 범위 밖.
- 어드민 API (`/api-admin/v1`): `X-Loopers-Ldap: loopers.admin` 헤더로 어드민 식별.

### 이번 스코프
- **포함**: 브랜드, 상품, 좋아요, 주문
- **제외**: 회원 (1주차 구현 완료), 결제 (추후 개발), 쿠폰 (추후 개발), 상품 옵션/카테고리

---

## 2. 액터 정의

| 액터 | 식별 방식 | 역할 |
|------|-----------|------|
| 구매자(User) | `X-Loopers-LoginId` + `X-Loopers-LoginPw` 헤더 | 상품 조회, 좋아요, 주문 |
| 어드민(Admin) | `X-Loopers-Ldap: loopers.admin` 헤더 | 브랜드/상품 CRUD, 주문 조회 |

---

## 3. 도메인별 기능 정의

### 3.1 브랜드 (Brand)

**고객 API** (`/api/v1`)

| METHOD | URI | 인증 | 설명 |
|--------|-----|---|------|
| GET | `/api/v1/brands/{brandId}` | X | 브랜드 정보 조회 |

**어드민 API** (`/api-admin/v1`)

| METHOD | URI | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/api-admin/v1/brands?page=0&size=20` | LDAP | 브랜드 목록 조회 (페이징) |
| GET | `/api-admin/v1/brands/{brandId}` | LDAP | 브랜드 상세 조회 |
| POST | `/api-admin/v1/brands` | LDAP | 브랜드 등록 |
| PUT | `/api-admin/v1/brands/{brandId}` | LDAP | 브랜드 수정 |
| DELETE | `/api-admin/v1/brands/{brandId}` | LDAP | 브랜드 삭제 |

**제약사항:**
- 브랜드 삭제 시 해당 브랜드의 상품도 함께 소프트 삭제 (`deletedAt` 처리)
- 고객에게는 브랜드명, 설명, 이미지 등 공개 정보만 노출
- 어드민에게는 등록일, 수정일, 삭제 여부 등 관리 정보 추가 노출

---

### 3.2 상품 (Product)

**고객 API**

| METHOD | URI | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/api/v1/products` | X | 상품 목록 조회 (필터/정렬/페이징) |
| GET | `/api/v1/products/{productId}` | X | 상품 상세 조회 |

**상품 목록 조회 쿼리 파라미터:**

| 파라미터 | 예시 | 설명 | 필수 |
|----------|------|------|------|
| `brandId` | `1` | 브랜드 필터링 | X |
| `sort` | `latest` / `price_asc` / `likes_desc` | 정렬 기준 (기본: latest) | X |
| `page` | `0` | 페이지 번호 (기본: 0) | X |
| `size` | `20` | 페이지당 수 (기본: 20) | X |

**어드민 API**

| METHOD | URI | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/api-admin/v1/products?page=0&size=20&brandId={brandId}` | LDAP | 상품 목록 조회 |
| GET | `/api-admin/v1/products/{productId}` | LDAP | 상품 상세 조회 |
| POST | `/api-admin/v1/products` | LDAP | 상품 등록 |
| PUT | `/api-admin/v1/products/{productId}` | LDAP | 상품 수정 |
| DELETE | `/api-admin/v1/products/{productId}` | LDAP | 상품 삭제 |

**제약사항:**
- 상품 등록 시 브랜드는 반드시 등록된 브랜드여야 함
- 상품의 브랜드는 등록 후 변경 불가
- 고객에게는 상품명, 가격, 설명, 이미지, 좋아요 수 노출
- 어드민에게는 추가로 재고 수량, 등록/수정일 등 관리 정보 노출
- 소프트 삭제된 상품은 고객 조회에서 제외

---

### 3.3 좋아요 (Like)

| METHOD | URI | 인증 | 설명 |
|--------|-----|------|------|
| POST | `/api/v1/products/{productId}/likes` | O | 좋아요 등록 |
| DELETE | `/api/v1/products/{productId}/likes` | O | 좋아요 취소 |
| GET | `/api/v1/users/{userId}/likes` | O | 내가 좋아요한 상품 목록 |

**제약사항:**
- 한 유저는 한 상품에 좋아요를 한 번만 등록 가능 (중복 불가)
- 이미 좋아요한 상품에 다시 POST → 에러 응답
- 좋아요하지 않은 상품에 DELETE → 에러 응답
- 좋아요 수는 상품 목록 정렬(`likes_desc`)에 활용됨
- 본인의 좋아요 목록만 조회 가능 (타 유저 접근 불가)

---

### 3.4 주문 (Order)

**고객 API**

| METHOD | URI | 인증 | 설명 |
|--------|-----|------|------|
| POST | `/api/v1/orders` | O | 주문 요청 |
| GET | `/api/v1/orders?startAt={date}&endAt={date}` | O | 내 주문 목록 조회 |
| GET | `/api/v1/orders/{orderId}` | O | 주문 상세 조회 |

**어드민 API**

| METHOD | URI | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/api-admin/v1/orders?page=0&size=20` | LDAP | 전체 주문 목록 |
| GET | `/api-admin/v1/orders/{orderId}` | LDAP | 주문 상세 조회 |

**주문 요청 바디 예시:**
```json
{
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ]
}
```

**제약사항:**
- 주문 생성 시 상품 재고 확인 및 즉시 차감
- 재고 부족 시 주문 실패
- 주문 정보에는 당시 상품 정보를 스냅샷으로 저장 (상품명, 가격, 브랜드명)
- 원본 상품이 수정/삭제되어도 주문 이력에는 영향 없음
- 주문 목록 조회의 날짜 필터는 주문 생성일 기준
- 본인의 주문만 조회 가능

---

## 4. 설계 결정 사항

| 항목 | 결정 | 이유 |
|------|------|------|
| 삭제 전략 | 소프트 삭제 (`deletedAt`) | `BaseEntity`에 이미 구현됨. 주문 스냅샷 참조 안전성 확보 |
| 재고 차감 시점 | 주문 생성 시 즉시 차감 | 결제 미구현 상태에서 가장 단순한 방식. 결제 도입 시 재검토 |
| 주문 스냅샷 범위 | 상품명 + 가격 + 수량 + 브랜드명 | 고객이 주문 이력에서 필요한 최소 정보 |
| 좋아요 수 집계 | 조회 시 COUNT 쿼리 | 초기엔 단순하게 시작. 성능 이슈 시 비정규화(`likeCount`) 검토 |
| 쿠폰 | 이번 스코프 제외 | API 목록에 미포함. 추후 별도 개발 |
| 상품 옵션 | 이번 스코프 제외 | 요구사항에 언급 없음 |

