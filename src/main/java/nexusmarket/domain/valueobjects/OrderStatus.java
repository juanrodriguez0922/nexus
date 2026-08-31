package nexusmarket.domain.valueobjects;

import java.util.Map;
import java.util.Set;

/**
 * Represents the lifecycle state of an order, the central business process of NexusMarket.
 *
 * <pre>CART → PENDING_PAYMENT → PAID → DISPATCHED → DELIVERED</pre>
 *
 * Once an order reaches the final state ({@code DELIVERED}) it must never be modified.
 */
public enum OrderStatus implements DomainCatalog {

    CART("CART", "Cart", "Provisional product selection, not yet confirmed as an order."),
    PENDING_PAYMENT("PENDING_PAYMENT", "Pending Payment",
            "Order confirmed by the buyer; awaiting financial confirmation."),
    PAID("PAID", "Paid", "Payment validated; fulfillment process has started."),
    DISPATCHED("DISPATCHED", "Dispatched", "Order has physically left the origin warehouse."),
    DELIVERED("DELIVERED", "Delivered", "Order delivery has been confirmed; the order is closed.");

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            CART, Set.of(PENDING_PAYMENT),
            PENDING_PAYMENT, Set.of(PAID),
            PAID, Set.of(DISPATCHED),
            DISPATCHED, Set.of(DELIVERED),
            DELIVERED, Set.of());

    private final String code;
    private final String name;
    private final String description;

    OrderStatus(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /** Indicates whether the transition to the target status is allowed by the lifecycle. */
    public boolean canTransitionTo(OrderStatus target) {
        return ALLOWED_TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }

    /** A final order can never be modified again. */
    public boolean isFinal() {
        return this == DELIVERED;
    }

    /** Resolves an order status from its business code. */
    public static OrderStatus fromCode(String code) {
        for (OrderStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown order status code: " + code);
    }
}