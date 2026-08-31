package nexusmarket.domain.valueobjects;

/** Represents the current execution state of a shipment. */
public enum ShipmentStatus implements DomainCatalog {

    PREPARING("PREPARING", "Preparing", "Order is being packed at the origin warehouse."),
    IN_TRANSIT("IN_TRANSIT", "In Transit", "Shipment has left the warehouse and is en route."),
    DELIVERED("DELIVERED", "Delivered", "Shipment has been confirmed as delivered to the buyer."),
    FAILED("FAILED", "Failed", "Delivery attempt failed and requires resolution.");

    private final String code;
    private final String name;
    private final String description;

    ShipmentStatus(String code, String name, String description) {
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

    /** Resolves a shipment status from its business code. */
    public static ShipmentStatus fromCode(String code) {
        for (ShipmentStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown shipment status code: " + code);
    }
}