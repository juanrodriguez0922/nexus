package nexusmarket.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import nexusmarket.domain.valueobjects.OrderStatus;

/**
 * Represents the formal commercial commitment made by a buyer. The order lifecycle
 * is the central business process of NexusMarket.
 *
 * <pre>CART → PENDING_PAYMENT → PAID → DISPATCHED → DELIVERED</pre>
 *
 * <p>Once an order reaches its final state it can no longer be modified under any
 * circumstance. The order coordinates its {@link Invoice}, {@link Shipment},
 * {@link Return} and {@link Refund} processes.</p>
 */
public class Order {

    private final String orderId;
    private final Buyer buyer;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus orderStatus;
    private final LocalDateTime createdAt;
    private Invoice invoice;
    private Shipment shipment;
    private final List<Return> returns = new ArrayList<>();

    /** Creates a confirmed order from cart selections (status PENDING_PAYMENT). */
    public Order(String orderId, Buyer buyer, List<CartItem> cartItems, LocalDateTime createdAt) {
        this(orderId, buyer, createdAt);
        Objects.requireNonNull(cartItems, "Cart items are required");
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("An order requires at least one item");
        }
        cartItems.stream().map(OrderItem::fromCartItem).forEach(items::add);
        this.orderStatus = OrderStatus.PENDING_PAYMENT;
        recalculateTotal();
    }

    /** Rebuilds a persisted order (used by persistence adapters). */
    public Order(String orderId, Buyer buyer, List<OrderItem> items, OrderStatus orderStatus,
                 LocalDateTime createdAt) {
        this(orderId, buyer, createdAt);
        Objects.requireNonNull(items, "Order items are required");
        if (items.isEmpty()) {
            throw new IllegalArgumentException("An order requires at least one item");
        }
        this.items.addAll(items);
        this.orderStatus = Objects.requireNonNull(orderStatus, "Order status is required");
        recalculateTotal();
    }

    private Order(String orderId, Buyer buyer, LocalDateTime createdAt) {
        Objects.requireNonNull(orderId, "Order id is required");
        Objects.requireNonNull(buyer, "Order buyer is required");
        Objects.requireNonNull(createdAt, "Order creation date is required");
        this.orderId = orderId;
        this.buyer = buyer;
        this.createdAt = createdAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public List<Return> getReturns() {
        return Collections.unmodifiableList(returns);
    }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void recalculateTotal() {
        getTotalAmount();
    }

    private void ensureNotFinalized() {
        if (orderStatus.isFinal()) {
            throw new IllegalStateException(
                    "Order " + orderId + " is finalized (Delivered) and can no longer be modified");
        }
    }

    /** Ensures the order belongs to the given buyer. */
    public void ensureOwnedBy(Buyer buyer) {
        if (!this.buyer.equals(buyer)) {
            throw new IllegalArgumentException(
                    "Order " + orderId + " does not belong to buyer "
                            + (buyer == null ? "?" : buyer.getUserId()));
        }
    }

    /**
     * Confirms payment and generates the invoice. An order cannot progress to Paid
     * without a validated payment.
     */
    public Invoice markAsPaid() {
        ensureNotFinalized();
        if (!orderStatus.canTransitionTo(OrderStatus.PAID)) {
            throw new IllegalStateException(
                    "Order " + orderId + " is in status " + orderStatus.getName()
                            + " but the operation requires status " + OrderStatus.PAID.getName());
        }
        orderStatus = OrderStatus.PAID;
        this.invoice = new Invoice(UUID.randomUUID().toString(), this, LocalDateTime.now());
        return invoice;
    }

    /**
     * Dispatches the order from a warehouse handled by a logistics operator. An order
     * cannot progress to Dispatched without confirmed warehouse stock reservation.
     */
    public Shipment dispatch(Warehouse originWarehouse, LogisticsOperator handledBy) {
        ensureNotFinalized();
        if (!orderStatus.canTransitionTo(OrderStatus.DISPATCHED)) {
            throw new IllegalStateException(
                    "Order " + orderId + " is in status " + orderStatus.getName()
                            + " but the operation requires status " + OrderStatus.DISPATCHED.getName());
        }
        Objects.requireNonNull(originWarehouse, "Origin warehouse is required");
        Objects.requireNonNull(handledBy, "Logistics operator is required");
        orderStatus = OrderStatus.DISPATCHED;
        this.shipment = new Shipment(UUID.randomUUID().toString(), this, originWarehouse,
                handledBy, LocalDateTime.now());
        return shipment;
    }

    /** Confirms delivery, closing the order. A delivered order is final. */
    public void markAsDelivered() {
        ensureNotFinalized();
        if (!orderStatus.canTransitionTo(OrderStatus.DELIVERED)) {
            throw new IllegalStateException(
                    "Order " + orderId + " is in status " + orderStatus.getName()
                            + " but the operation requires status " + OrderStatus.DELIVERED.getName());
        }
        orderStatus = OrderStatus.DELIVERED;
        if (shipment != null) {
            shipment.markDelivered(LocalDateTime.now());
        }
    }

    /** Requests a return; only delivered orders can originate returns. */
    public Return requestReturn(String reason) {
        if (orderStatus != OrderStatus.DELIVERED) {
            throw new IllegalStateException(
                    "Return not allowed for order " + orderId + ": returns can only be requested for delivered orders");
        }
        Return requested = new Return(UUID.randomUUID().toString(), this, reason,
                LocalDateTime.now());
        returns.add(requested);
        return requested;
    }

    void attachReturn(Return requested) {
        if (requested != null && !returns.contains(requested)) {
            returns.add(requested);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Order order)) {
            return false;
        }
        return orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "Order{orderId='" + orderId + "', buyer=" + buyer.getUserId()
                + ", status=" + orderStatus + ", total=" + getTotalAmount() + "}";
    }
}