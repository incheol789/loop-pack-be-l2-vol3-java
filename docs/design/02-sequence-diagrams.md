# 시퀀스 다이어그램

> 주요 유스케이스별 시퀀스 다이어그램 (Mermaid)

---

## 1. 주문 생성 플로우

### 목적
주문 생성 시 재고 확인 → 차감 → 스냅샷 저장이 하나의 트랜잭션 안에서 어떤 순서로 일어나는지 확인한다.
특히 재고 부족 시 실패 흐름과, 여러 상품을 한 번에 처리할 때의 경계를 검증한다.

```mermaid
sequenceDiagram
    actor User as 구매자
    participant API as OrderController
    participant OS as OrderService
    participant PS as ProductService
    participant DB as Database

    User->>API: POST /api/v1/orders (items[])
    API->>OS: createOrder(loginId, items)

    rect rgb(230, 245, 255)
        Note over OS,DB: 트랜잭션 시작
        loop 각 주문 항목
            OS->>PS: getProduct(productId)
            PS->>DB: SELECT product
            DB-->>PS: Product
            PS-->>OS: Product

            OS->>PS: decreaseStock(productId, quantity)
            PS->>DB: UPDATE stock (재고 차감)

            alt 재고 부족
                DB-->>PS: 실패
                PS-->>OS: 재고 부족 예외
                OS-->>API: 주문 실패
                API-->>User: 400 Bad Request
            end
        end

        OS->>DB: INSERT order
        OS->>DB: INSERT order_items (상품 스냅샷 포함)
        Note over OS,DB: 트랜잭션 커밋
    end

    OS-->>API: Order
    API-->>User: 201 Created (주문 정보)
```

### 핵심 포인트
- 재고 확인과 차감은 **같은 트랜잭션** 안에서 수행하여 정합성을 보장한다
- 여러 상품 중 하나라도 재고 부족이면 **전체 주문이 롤백**된다
- 주문 항목(order_item)에는 당시 상품명, 가격, 브랜드명이 스냅샷으로 저장된다

---

## 2. 브랜드 삭제 플로우

### 목적
브랜드 삭제 시 하위 상품들이 연쇄적으로 소프트 삭제되는 흐름을 확인한다.
삭제가 기존 주문 데이터에 영향을 주지 않는 구조인지 검증한다.

```mermaid
sequenceDiagram
    actor Admin as 어드민
    participant API as BrandAdminController
    participant BS as BrandService
    participant PS as ProductService
    participant DB as Database

    Admin->>API: DELETE /api-admin/v1/brands/{brandId}
    API->>BS: deleteBrand(brandId)

    rect rgb(255, 245, 230)
        Note over BS,DB: 트랜잭션 시작
        BS->>DB: SELECT brand
        DB-->>BS: Brand

        alt 브랜드 없음 or 이미 삭제됨
            BS-->>API: 404 Not Found
            API-->>Admin: 404
        end

        BS->>PS: deleteProductsByBrand(brandId)
        PS->>DB: UPDATE products SET deleted_at = now() WHERE brand_id = ?
        DB-->>PS: 완료

        BS->>DB: UPDATE brand SET deleted_at = now()
        Note over BS,DB: 트랜잭션 커밋
    end

    BS-->>API: 완료
    API-->>Admin: 200 OK
```

### 핵심 포인트
- **소프트 삭제** 방식이므로 데이터는 DB에 남아있고, 기존 주문 스냅샷에 영향 없음
- 브랜드와 상품의 삭제가 **하나의 트랜잭션**으로 묶여 중간 실패 시 모두 롤백됨
- 삭제된 상품은 고객 조회(`deletedAt IS NULL` 필터)에서 자동 제외

---

## 3. 좋아요 등록/취소 플로우

### 목적
좋아요 등록과 취소 시 중복 방지 로직이 어떻게 동작하는지 확인한다.

```mermaid
sequenceDiagram
    actor User as 구매자
    participant API as LikeController
    participant LS as LikeService
    participant DB as Database

    Note over User,DB: 좋아요 등록
    User->>API: POST /api/v1/products/{productId}/likes
    API->>LS: addLike(userId, productId)
    LS->>DB: SELECT like WHERE user_id = ? AND product_id = ?

    alt 이미 좋아요 존재
        DB-->>LS: Like 존재
        LS-->>API: 중복 좋아요 예외
        API-->>User: 409 Conflict
    else 좋아요 없음
        DB-->>LS: null
        LS->>DB: INSERT like
        LS-->>API: 완료
        API-->>User: 201 Created
    end

    Note over User,DB: 좋아요 취소
    User->>API: DELETE /api/v1/products/{productId}/likes
    API->>LS: removeLike(userId, productId)
    LS->>DB: SELECT like WHERE user_id = ? AND product_id = ?

    alt 좋아요 없음
        DB-->>LS: null
        LS-->>API: 좋아요 없음 예외
        API-->>User: 404 Not Found
    else 좋아요 존재
        DB-->>LS: Like
        LS->>DB: DELETE like
        LS-->>API: 완료
        API-->>User: 200 OK
    end
```

### 핵심 포인트
- 좋아요는 **물리 삭제** — 소프트 삭제와 달리 취소하면 row 자체를 제거한다 (좋아요 이력 보존 불필요)
- `(user_id, product_id)` 유니크 제약으로 DB 레벨에서도 중복 방지
- 좋아요 수 집계는 좋아요 테이블의 COUNT로 처리 (비정규화는 추후 검토)
