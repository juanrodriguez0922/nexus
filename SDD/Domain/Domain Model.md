# Domain Model

## Introduction

The Domain Model represents the core business entities of the **NexusMarket** system, a digital marketplace platform that intermediates commercial transactions between buyers and sellers, covering user management, product cataloging, distributed inventory, order lifecycle, invoicing, logistics, and after-sales processes.

These entities encapsulate the business rules, data, relationships, and lifecycle concepts described in the NexusMarket functional specification.

The model follows Object-Oriented Design and Domain-Driven Design (DDD) principles. Inheritance is used to represent genuine domain specialization, while explicit object relationships are preferred over generic identifier fields.

The model distinguishes between:

* **Users**, which represent identifiable people authorized to interact with the system and their role within it.
* **Buyers**, **Sellers**, **Logistics Operators**, **Administrators**, and **Supervisors**, which represent role-based specializations of a user, each with distinct responsibilities and data.
* **Warehouses**, which represent physical storage locations belonging either to the Marketplace or to a Seller.
* **Products**, which represent the physical or digital goods offered through the catalog.
* **Inventory**, which represents distributed stock, always linked to a product and a warehouse.
* **Orders**, which represent the formal commercial commitment between a buyer and the marketplace, from cart to delivery.
* **Invoices**, **Shipments**, **Returns**, and **Refunds**, which represent the commercial, logistical, and post-sale processes generated throughout an order's lifecycle.

A user interacts with the marketplace strictly according to a single assigned role. Every order progresses through a controlled lifecycle, and inventory must never fall below zero.

---

# Domain Class Hierarchy

```text
User (Abstract)
├── Buyer
├── Seller
├── LogisticsOperator
├── Administrator
└── Supervisor

Warehouse

Product
Inventory
InventoryMovement

ShoppingCart
Order

Invoice
Shipment
Return
Refund
```

---

# Domain Relationships

```text
User
   │
   ├── Buyer
   ├── Seller
   ├── LogisticsOperator
   ├── Administrator
   └── Supervisor

Administrator
   │
   └── registers ─────────────> Seller

Seller
   │
   ├── owns ──────────────────> Warehouse
   └── owns ──────────────────> Product

Warehouse
   │
   └── holds ─────────────────> Inventory

Product
   │
   └── stocked as ─────────────> Inventory

Inventory
   │
   └── tracked by ─────────────> InventoryMovement

Buyer
   │
   ├── owns ──────────────────> ShoppingCart
   └── places ────────────────> Order

ShoppingCart
   │
   └── converted into ────────> Order

Order
   │
   ├── contains ───────────────> OrderItem ──> Product
   ├── generates ──────────────> Invoice
   ├── generates ──────────────> Shipment
   └── may generate ───────────> Return ────> Refund

Shipment
   │
   └── handled by ─────────────> LogisticsOperator

Shipment
   │
   └── departs from ───────────> Warehouse
```

---

# Entities

---

# User (Abstract)

## Description

Represents any person authorized to interact with the NexusMarket system.

This abstract class centralizes the common identity, contact, and access information shared by all marketplace participants.

The role assigned to a user represents what that user means within the system and determines the responsibilities and business capabilities associated with that user. Each user has exactly one role.

This class cannot be instantiated directly.

## Attributes

| Attribute  | Type       | Description                                                              |
| ---------- | ---------- | ------------------------------------------------------------------------- |
| userId     | String     | Unique identifier of the user.                                            |
| fullName   | String     | Official full name of the user.                                          |
| email      | String     | Primary access and communication email. Must be unique in the platform.  |
| identityDocument | String | National identification document. Must be unique in the platform.        |
| role       | UserRole   | Business role that defines the user's responsibilities within the system. |
| status     | UserStatus | Current operational status of the user (Active, Blocked, Inactive).       |

## Relationships

* A `User` is specialized into exactly one of `Buyer`, `Seller`, `LogisticsOperator`, `Administrator`, or `Supervisor`.
* The `role` belongs to `User` because it represents the user's meaning and responsibilities within the marketplace.

## Business Rules

* Each user has a single, exclusive role within the system.
* A user must never manage information outside the scope of their role.
* `email` and `identityDocument` must be unique across the platform.

---

# Buyer

## Description

Represents a user who purchases products published on the marketplace.

A buyer may register multiple delivery addresses and maintains a commercial status independent of their general account status.

A buyer must never manage information belonging to other buyers or to inventory.

## Inherits From

`User`

## Attributes

| Attribute            | Type                | Description                                                     |
| -------------------- | ------------------- | ---------------------------------------------------------------- |
| primaryAddress        | String              | Usual delivery address.                                          |
| additionalAddresses   | List\<String\>      | Secondary delivery addresses. Empty by default.                  |
| commercialStatus      | BuyerCommercialStatus | Buyer's current eligibility to place purchases.                |
| cart                  | ShoppingCart        | Buyer's active shopping cart. Created on demand.                 |
| orders                | List\<Order\>       | Orders placed by the buyer. Empty by default.                    |

## Relationships

* A `Buyer` owns exactly one active `ShoppingCart`.
* A `Buyer` places zero or more `Order` instances.

---

# Seller

## Description

Represents a user responsible for registering and managing products on the marketplace.

Sellers cannot self-register; they are incorporated into the platform exclusively by an `Administrator`.

## Inherits From

`User`

## Attributes

| Attribute   | Type                  | Description                                        |
| ----------- | --------------------- | --------------------------------------------------- |
| registeredBy | Administrator         | Administrator who incorporated the seller.          |
| warehouses  | List\<Warehouse\>     | Warehouses owned by the seller. Empty by default.   |
| products    | List\<Product\>       | Products published by the seller. Empty by default. |

## Relationships

* A `Seller` is registered by exactly one `Administrator`.
* A `Seller` owns zero or more `Warehouse` instances.
* A `Seller` owns zero or more `Product` instances.

## Business Rule

```text
A Seller cannot be created without an associated Administrator
who performed the registration.
```

---

# LogisticsOperator

## Description

Represents a user responsible for the physical operation of warehouses and the dispatch of orders.

## Inherits From

`User`

## Attributes

| Attribute        | Type               | Description                                             |
| ---------------- | ------------------ | -------------------------------------------------------- |
| assignedWarehouses | List\<Warehouse\> | Warehouses the operator is responsible for. Empty by default. |

## Relationships

* A `LogisticsOperator` is assigned to zero or more `Warehouse` instances.
* A `LogisticsOperator` handles zero or more `Shipment` instances.

---

# Administrator

## Description

Represents a user responsible for onboarding sellers and administering warehouses.

## Inherits From

`User`

## Relationships

* An `Administrator` registers zero or more `Seller` instances.
* An `Administrator` may register Marketplace-owned `Warehouse` instances.

---

# Supervisor

## Description

Represents a read-only, operational oversight profile used for monitoring and reporting purposes.

Supervisors do not create or modify commercial or operational data; they consult consolidated administrative information.

## Inherits From

`User`

---

# Warehouse

## Description

Represents a physical location used to store and manage inventory.

Warehouses are classified as either Marketplace-owned or Seller-owned.

## Attributes

| Attribute      | Type          | Description                                            |
| -------------- | ------------- | -------------------------------------------------------- |
| warehouseId    | String        | Unique identifier of the warehouse.                       |
| name           | String        | Display name of the warehouse.                             |
| location       | String        | Physical address of the warehouse.                          |
| warehouseType  | WarehouseType | Classification of the warehouse (Marketplace or Seller).    |
| owner          | Seller?       | Seller who owns the warehouse. Null for Marketplace warehouses. |

## Relationships

* A `Warehouse` may belong to zero or one `Seller`. Marketplace warehouses have no seller owner.
* A `Warehouse` holds zero or more `Inventory` records.
* A `Warehouse` may be assigned zero or more `LogisticsOperator` instances.
* A `Warehouse` may be the origin of multiple `Shipment` instances.

---

# Product

## Description

Represents a physical or digital good offered through the marketplace catalog.

Physical products require inventory management and dispatch. Digital products are delivered immediately after payment and do not require warehouse stock.

## Attributes

| Attribute     | Type                       | Description                                                  |
| ------------- | -------------------------- | -------------------------------------------------------------- |
| productId     | String                     | Unique identifier of the product.                                |
| name          | String                     | Commercial name of the product.                                  |
| productType   | ProductType                | Classification of the product (Physical or Digital).              |
| variants      | List\<ProductVariant\>     | Variations of the product (color, size, model, etc.). Empty by default. |
| status        | ProductStatus              | Current publication status of the product.                        |
| seller        | Seller                     | Seller who owns and publishes the product.                        |
| price         | BigDecimal                 | Current sale price of the product.                                 |

## Relationships

* A `Product` belongs to one `Seller`.
* A `Product`, when physical, is stocked through zero or more `Inventory` records.
* A `Product` may appear in multiple `CartItem` and `OrderItem` instances.

---

# ProductVariant (Value Object)

## Description

Represents a specific variation of a product, such as a color, size, or model.

This is a Value Object embedded within `Product`; it has no independent identity outside the product it describes.

## Attributes

| Attribute    | Type   | Description                                        |
| ------------ | ------ | ---------------------------------------------------- |
| attributeName | String | Name of the varying attribute (e.g., "Color", "Size"). |
| value        | String | Value of the attribute (e.g., "Red", "XL").             |
| sku          | String | Stock-keeping unit identifying this specific variant.   |

---

# Inventory

## Description

Represents distributed stock of a physical product, always linked to exactly one product and one warehouse.

Inventory quantities must never become negative under any circumstance.

## Attributes

| Attribute       | Type      | Description                                        |
| --------------- | --------- | ----------------------------------------------------- |
| inventoryId     | String    | Unique identifier of the inventory record.               |
| product         | Product   | Product this inventory record belongs to.                |
| warehouse       | Warehouse | Warehouse where the stock is physically held.             |
| availableQuantity | Integer | Quantity currently available for sale.                    |
| reservedQuantity  | Integer | Quantity reserved by pending orders.                       |

## Relationships

* An `Inventory` record references exactly one `Product`.
* An `Inventory` record references exactly one `Warehouse`.
* An `Inventory` record generates zero or more `InventoryMovement` instances.

## Business Rules

```text
Inventory.availableQuantity must never be negative.

Inventory cannot be reserved when it is insufficient
or marked as "Damaged".
```

---

# InventoryMovement

## Description

Represents a significant event that changes the quantity of an inventory record.

An inventory movement provides traceability distinct from the current stock level: `Inventory.availableQuantity` represents the current state, while `InventoryMovement` represents the event that caused a change to that state.

## Attributes

| Attribute     | Type                  | Description                                     |
| ------------- | --------------------- | -------------------------------------------------- |
| movementId    | String                | Unique identifier of the movement.                   |
| inventory     | Inventory             | Inventory record affected by the movement.            |
| movementType  | InventoryMovementType | Category of the movement (Inbound, Reservation, Outbound, Adjustment, Return). |
| quantity      | Integer               | Quantity involved in the movement.                     |
| movementDate  | LocalDateTime         | Date and time the movement occurred.                    |
| performedBy   | User                  | User who triggered or authorized the movement.           |

## Relationships

* Each `InventoryMovement` affects exactly one `Inventory` record.
* Each `InventoryMovement` is performed by one `User`.

---

# ShoppingCart

## Description

Represents the provisional, pre-purchase selection of products made by a buyer.

A shopping cart is converted into an `Order` once the buyer confirms the purchase.

## Attributes

| Attribute  | Type              | Description                                   |
| ---------- | ----------------- | ------------------------------------------------ |
| cartId     | String            | Unique identifier of the cart.                     |
| buyer      | Buyer             | Buyer who owns the cart.                            |
| items      | List\<CartItem\>  | Products currently selected. Empty by default.        |
| createdAt  | LocalDateTime     | Date and time the cart was created.                    |

## Relationships

* A `ShoppingCart` belongs to exactly one `Buyer`.
* A `ShoppingCart` contains zero or more `CartItem` instances.

---

# CartItem (Value Object)

## Description

Represents a single product selection within a shopping cart, including the requested quantity and variant.

## Attributes

| Attribute | Type            | Description                          |
| --------- | --------------- | --------------------------------------- |
| product   | Product         | Selected product.                          |
| variant   | ProductVariant? | Selected variant, if applicable.             |
| quantity  | Integer         | Quantity requested by the buyer.               |

---

# Order

## Description

Represents the formal commercial commitment made by a buyer. The order lifecycle is the central business process of NexusMarket.

Once an order reaches its final state, it can no longer be modified under any circumstance.

## Attributes

| Attribute     | Type              | Description                                            |
| ------------- | ----------------- | --------------------------------------------------------- |
| orderId       | String            | Unique identifier of the order.                              |
| buyer         | Buyer             | Buyer who placed the order.                                    |
| items         | List\<OrderItem\> | Products included in the order.                                 |
| orderStatus   | OrderStatus       | Current state of the order's lifecycle.                          |
| totalAmount   | BigDecimal        | Total amount of the order.                                        |
| createdAt     | LocalDateTime     | Date and time the order was created.                               |
| invoice       | Invoice?          | Invoice generated once the order is paid.                          |
| shipment      | Shipment?         | Shipment generated for physical products.                          |

## Relationships

* An `Order` is placed by one `Buyer`.
* An `Order` contains one or more `OrderItem` instances.
* An `Order` generates zero or one `Invoice`.
* An `Order` generates zero or one `Shipment`.
* An `Order` may generate zero or more `Return` instances.

## Business Rules

```text
A finalized Order (Delivered/Completed) must never be modified.

An Order cannot progress to Paid without a validated payment.

An Order cannot progress to Dispatched without confirmed
warehouse stock reservation.
```

---

# OrderItem (Value Object)

## Description

Represents a single product line within a confirmed order, preserving the price and quantity at the moment of purchase.

## Attributes

| Attribute   | Type            | Description                              |
| ----------- | --------------- | ------------------------------------------- |
| product     | Product         | Ordered product.                               |
| variant     | ProductVariant? | Ordered variant, if applicable.                  |
| quantity    | Integer         | Quantity ordered.                                 |
| unitPrice   | BigDecimal      | Price per unit at the time of the order.            |

---

# Invoice

## Description

Represents the commercial billing information associated with a paid order.

## Attributes

| Attribute     | Type          | Description                                 |
| ------------- | ------------- | ---------------------------------------------- |
| invoiceId     | String        | Unique identifier of the invoice.                 |
| order         | Order         | Order this invoice was generated for.                |
| issueDate     | LocalDateTime | Date and time the invoice was issued.                 |
| totalAmount   | BigDecimal    | Total invoiced amount.                                 |
| invoiceStatus | InvoiceStatus | Current status of the invoice.                          |

## Relationships

* An `Invoice` is generated from exactly one `Order`.

---

# Shipment

## Description

Represents the logistical process required to pack, dispatch, and deliver the physical products of an order.

## Attributes

| Attribute        | Type              | Description                                        |
| ---------------- | ----------------- | ------------------------------------------------------ |
| shipmentId       | String            | Unique identifier of the shipment.                        |
| order            | Order             | Order this shipment fulfills.                              |
| originWarehouse  | Warehouse         | Warehouse from which the order is dispatched.                |
| handledBy        | LogisticsOperator | Logistics operator responsible for the shipment.               |
| shipmentStatus   | ShipmentStatus    | Current execution status of the shipment.                       |
| dispatchDate     | LocalDateTime?    | Date and time the shipment left the warehouse.                    |
| deliveryDate     | LocalDateTime?    | Date and time the shipment was confirmed delivered.                 |

## Relationships

* A `Shipment` fulfills exactly one `Order`.
* A `Shipment` departs from one `Warehouse`.
* A `Shipment` is handled by one `LogisticsOperator`.

---

# Return

## Description

Represents a buyer-initiated request to return one or more products from a delivered order.

## Attributes

| Attribute    | Type         | Description                                    |
| ------------ | ------------ | -------------------------------------------------- |
| returnId     | String       | Unique identifier of the return request.               |
| order        | Order        | Order the return applies to.                             |
| reason       | String       | Reason provided by the buyer for the return.               |
| returnStatus | ReturnStatus | Current status of the return request.                       |
| requestedAt  | LocalDateTime | Date and time the return was requested.                      |

## Relationships

* A `Return` applies to exactly one `Order`.
* A `Return` may generate zero or one `Refund`.

---

# Refund

## Description

Represents the monetary reimbursement issued to a buyer as a result of an approved return.

## Attributes

| Attribute    | Type         | Description                                   |
| ------------ | ------------ | ------------------------------------------------- |
| refundId     | String       | Unique identifier of the refund.                      |
| relatedReturn | Return      | Return that originated the refund.                       |
| amount       | BigDecimal   | Amount reimbursed to the buyer.                            |
| refundStatus | RefundStatus | Current status of the refund process.                        |
| processedAt  | LocalDateTime? | Date and time the refund was processed.                     |

## Relationships

* A `Refund` originates from exactly one `Return`.

---

# Domain Lifecycle Relationship

The general lifecycle of a commercial order is:

```text
ShoppingCart
      │
      │ buyer confirms purchase
      ▼
    Order
      │
      ├── Cart
      ├── PendingPayment
      ├── Paid ─────────────> Invoice generated
      ├── Dispatched ───────> Shipment generated
      └── Delivered / Completed
```

For example, when an order is paid:

```text
Order
    │
    │ orderStatus changes
    ▼
  PAID
    │
    └── Invoice
           order = Order
           totalAmount = Order.totalAmount
           issueDate = now
```

Similarly, when a shipment is dispatched:

```text
Order
    │
    │ orderStatus changes
    ▼
DISPATCHED
    │
    └── Shipment
           order = Order
           originWarehouse = Warehouse
           handledBy = LogisticsOperator
           shipmentStatus = IN_TRANSIT
```

---

# Domain Design Rules

## User and Roles

* All marketplace participants (`Buyer`, `Seller`, `LogisticsOperator`, `Administrator`, `Supervisor`) inherit from `User`.
* `role` is defined in `User` and determines the participant's responsibilities.
* A user must never operate outside the boundaries of their assigned role.
* `Seller` instances must always reference the `Administrator` who registered them.

## Warehouses and Ownership

* `Warehouse.owner` is null for Marketplace-owned warehouses and set for Seller-owned warehouses.
* `LogisticsOperator` instances operate over one or more warehouses, regardless of ownership.

## Catalog and Inventory

* `Product` distinguishes Physical and Digital types; only Physical products require `Inventory`.
* `Inventory` must always reference exactly one `Product` and one `Warehouse`.
* Inventory quantities must never be negative.
* Reservation of inventory that is insufficient or marked as damaged must be rejected.

## Orders and Post-Sale

* `Order` is the central aggregate coordinating `Invoice`, `Shipment`, `Return`, and `Refund`.
* A finalized `Order` (Delivered/Completed) must never be modified.
* `Return` and `Refund` are dependent processes that can only originate from an existing `Order`.