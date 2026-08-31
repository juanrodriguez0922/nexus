package nexusmarket.domain.valueobjects;

/**
 * Represents the type of event that changes the quantity held in an inventory record.
 *
 * <p>Movement types are independent from the inventory's current quantity: a
 * {@code quantity} represents the current state, while a {@code movement} represents
 * the event that caused a change to that state.</p>
 */
public enum InventoryMovementType implements DomainCatalog {

    INBOUND("INBOUND", "Inbound", "Entry of new stock into a warehouse."),
    RESERVATION("RESERVATION", "Reservation", "Temporary hold of stock for a pending order."),
    OUTBOUND("OUTBOUND", "Outbound", "Removal of stock due to a completed sale."),
    ADJUSTMENT("ADJUSTMENT", "Adjustment", "Manual correction of recorded stock."),
    RETURN("RETURN", "Return", "Reintroduction of stock resulting from an approved return.");

    private final String code;
    private final String name;
    private final String description;

    InventoryMovementType(String code, String name, String description) {
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

    /** Resolves an inventory movement type from its business code. */
    public static InventoryMovementType fromCode(String code) {
        for (InventoryMovementType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown inventory movement type code: " + code);
    }
}