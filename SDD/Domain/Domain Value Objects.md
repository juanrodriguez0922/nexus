# Domain Value Objects

## Introduction

Value Objects represent immutable concepts within the NexusMarket domain.

Unlike Entities, Value Objects do not have their own identity. They are defined entirely by their values and are used to encapsulate controlled business concepts, improve domain expressiveness, and prevent the use of primitive values or scattered string literals throughout the application.

The marketplace domain uses Value Objects for business catalogs such as roles, statuses, product classifications, movement types, and order states.

All business catalogs inherit from `DomainCatalog`.

---

# Value Object Hierarchy

```text
DomainCatalog (Abstract)
├── UserRole
├── UserStatus
├── BuyerCommercialStatus
├── WarehouseType
├── ProductType
├── ProductStatus
├── InventoryMovementType
├── OrderStatus
├── InvoiceStatus
├── ShipmentStatus
├── ReturnStatus
└── RefundStatus
```

---

# DomainCatalog (Abstract)

## Description

Represents a generic business catalog used throughout the NexusMarket domain.

`DomainCatalog` provides a consistent structure for controlled business values that require a code, human-readable name, and business description.

This class cannot be instantiated directly.

## Attributes

| Attribute   | Type   | Description                                           |
| ----------- | ------ | ----------------------------------------------------- |
| code        | String | Unique business identifier of the catalog value.      |
| name        | String | Human-readable name displayed within the application. |
| description | String | Business definition of the catalog value.             |

## Characteristics

* Immutable.
* Equality is determined by value rather than object identity.
* Catalog values are controlled by the domain.
* Catalog values must not be represented by arbitrary strings throughout the application.
* Each catalog value must have a unique `code`.

---

# UserRole

## Description

Represents the single responsibility and permission set assigned to a user within the marketplace.

Each user has exactly one role, and a user must never manage information outside the scope of that role.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code               | Name                | Description                                                       |
| ------------------ | ------------------- | -------------------------------------------------------------------- |
| BUYER              | Buyer               | User who purchases published products.                                  |
| SELLER             | Seller              | User responsible for registering and administering their products.       |
| LOGISTICS_OPERATOR | Logistics Operator  | User responsible for the physical operation of warehouses and dispatch.    |
| ADMINISTRATOR      | Administrator       | User responsible for administering sellers and warehouses.                  |
| SUPERVISOR         | Supervisor          | Read-only profile for operational consultation and follow-up.                |

---

# UserStatus

## Description

Represents the current operational status of a user's access to the marketplace.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code     | Name     | Description                                              |
| -------- | -------- | ----------------------------------------------------------- |
| ACTIVE   | Active   | User can access and operate within the system normally.        |
| INACTIVE | Inactive | User exists but is not currently enabled for operations.        |
| BLOCKED  | Blocked  | User access has been suspended.                                  |

---

# BuyerCommercialStatus

## Description

Represents a buyer's current eligibility to place purchases on the marketplace.

`BuyerCommercialStatus` is independent from `UserStatus`. A buyer's general account may be active while their commercial status restricts purchasing, or vice versa, depending on business rules.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code     | Name     | Description                                              |
| -------- | -------- | ----------------------------------------------------------- |
| ENABLED  | Enabled  | Buyer is authorized to create carts and place orders.           |
| RESTRICTED | Restricted | Buyer may browse the catalog but cannot complete purchases. |
| SUSPENDED  | Suspended  | Buyer's purchasing privileges have been revoked.               |

---

# WarehouseType

## Description

Represents the ownership classification of a warehouse.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code        | Name        | Description                                     |
| ----------- | ----------- | -------------------------------------------------- |
| MARKETPLACE | Marketplace | Warehouse owned and operated by the marketplace.      |
| SELLER      | Seller      | Warehouse owned and operated by an individual seller.  |

---

# ProductType

## Description

Represents the fulfillment classification of a product.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code     | Name     | Description                                                  |
| -------- | -------- | ---------------------------------------------------------------- |
| PHYSICAL | Physical | Tangible good requiring inventory management and dispatch.         |
| DIGITAL  | Digital  | Intangible good delivered immediately after payment confirmation.    |

---

# ProductStatus

## Description

Represents the current publication state of a product within the catalog.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code          | Name          | Description                                            |
| ------------- | ------------- | ---------------------------------------------------------- |
| PUBLISHED     | Published     | Product is visible and available in the public catalog.       |
| SUSPENDED     | Suspended     | Product is temporarily hidden from the public catalog.          |
| DISCONTINUED  | Discontinued  | Product is permanently retired from the catalog.                 |

---

# InventoryMovementType

## Description

Represents the type of event that changes the quantity held in an inventory record.

Movement types are independent from the inventory's current quantity: a **quantity** represents the current state, while a **movement** represents the event that caused a change to that state.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code        | Name        | Description                                             |
| ----------- | ----------- | ---------------------------------------------------------- |
| INBOUND     | Inbound     | Entry of new stock into a warehouse.                            |
| RESERVATION | Reservation | Temporary hold of stock for a pending order.                       |
| OUTBOUND    | Outbound    | Removal of stock due to a completed sale.                            |
| ADJUSTMENT  | Adjustment  | Manual correction of recorded stock.                                   |
| RETURN      | Return      | Reintroduction of stock resulting from an approved return.                |

---

# OrderStatus

## Description

Represents the lifecycle state of an order, the central business process of NexusMarket.

Once an order reaches a final state (`DELIVERED`), it must never be modified.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code             | Name              | Description                                                |
| ---------------- | ----------------- | -------------------------------------------------------------- |
| CART              | Cart              | Provisional product selection, not yet confirmed as an order.       |
| PENDING_PAYMENT   | Pending Payment   | Order confirmed by the buyer; awaiting financial confirmation.        |
| PAID              | Paid              | Payment validated; fulfillment process has started.                     |
| DISPATCHED        | Dispatched        | Order has physically left the origin warehouse.                          |
| DELIVERED         | Delivered         | Order delivery has been confirmed; the order is closed.                    |

## Lifecycle

```text
CART
  │
  ▼
PENDING_PAYMENT
  │
  ▼
PAID
  │
  ▼
DISPATCHED
  │
  ▼
DELIVERED
```

---

# InvoiceStatus

## Description

Represents the current state of an invoice generated for a paid order.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code      | Name      | Description                                       |
| --------- | --------- | ----------------------------------------------------- |
| ISSUED    | Issued    | Invoice has been generated and delivered to the buyer.  |
| CANCELLED | Cancelled | Invoice was cancelled, typically due to an order issue.    |

---

# ShipmentStatus

## Description

Represents the current execution state of a shipment.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code        | Name        | Description                                              |
| ----------- | ----------- | ---------------------------------------------------------- |
| PREPARING   | Preparing   | Order is being packed at the origin warehouse.                 |
| IN_TRANSIT  | In Transit  | Shipment has left the warehouse and is en route.                  |
| DELIVERED   | Delivered   | Shipment has been confirmed as delivered to the buyer.               |
| FAILED      | Failed      | Delivery attempt failed and requires resolution.                       |

---

# ReturnStatus

## Description

Represents the current state of a buyer-initiated return request.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code       | Name       | Description                                            |
| ---------- | ---------- | ----------------------------------------------------------- |
| REQUESTED  | Requested  | Buyer has submitted a return request.                            |
| APPROVED   | Approved   | Seller or Administrator approved the return.                        |
| REJECTED   | Rejected   | Seller or Administrator rejected the return.                          |
| COMPLETED  | Completed  | Returned product has been received and processed.                       |

---

# RefundStatus

## Description

Represents the current state of a monetary reimbursement associated with an approved return.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code       | Name       | Description                                        |
| ---------- | ---------- | ------------------------------------------------------ |
| REQUESTED  | Requested  | Refund has been requested following an approved return.  |
| APPROVED   | Approved   | Refund has been authorized by Seller or Administrator.       |
| PROCESSED  | Processed  | Funds have been returned to the buyer.                          |
| REJECTED   | Rejected   | Refund request has been denied.                                   |

---

# Primitive Enumerations

The following concepts are represented as primitive enumerations because they contain fixed technical values and do not require business catalog metadata such as `code`, `name`, or `description`.

---

# ApprovalDecision

## Description

Represents the result of an approval process, used by Sellers and Administrators when reviewing returns and refunds.

## Values

```text
APPROVED
REJECTED
```

---

# NotificationChannel

## Description

Represents the communication channel used by the system to notify buyers and sellers about order, shipment, and refund events.

## Values

```text
EMAIL
SMS
PUSH_NOTIFICATION
```

---

# Value Object Design Rules

## Immutability

All Value Objects must be immutable after creation.

Their values cannot be modified after the object has been instantiated.

## Equality

Value Objects are compared according to their values rather than object identity.

Two instances containing the same business values represent the same Value Object.

## Controlled Values

Business catalogs must use controlled values defined by the domain.

The application must avoid replacing these concepts with arbitrary strings such as:

```text
"ACTIVE"
"PAID"
"APPROVED"
```

throughout the codebase.

Instead, the corresponding Value Object must be used:

```text
UserStatus
BuyerCommercialStatus
ProductStatus
OrderStatus
ShipmentStatus
ReturnStatus
RefundStatus
```

## Business Versus Technical Enumerations

A business concept should be modeled as a `DomainCatalog` Value Object when it requires:

* a business code;
* a display name;
* a business description;
* controlled domain evolution.

A simple enumeration should be used when the concept represents a fixed technical value without additional business metadata.

## Relationship With Entities

Entities reference Value Objects rather than primitive strings whenever the referenced value represents a controlled business concept.

Examples:

```text
User.role : UserRole

User.status : UserStatus

Buyer.commercialStatus : BuyerCommercialStatus

Warehouse.warehouseType : WarehouseType

Product.productType : ProductType

Product.status : ProductStatus

InventoryMovement.movementType : InventoryMovementType

Order.orderStatus : OrderStatus

Invoice.invoiceStatus : InvoiceStatus

Shipment.shipmentStatus : ShipmentStatus

Return.returnStatus : ReturnStatus

Refund.refundStatus : RefundStatus
```

This approach improves type safety, domain expressiveness, maintainability, and consistency with Domain-Driven Design principles.