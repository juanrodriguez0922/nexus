package nexusmarket.domain.valueobjects;

/**
 * Represents the ownership classification of a warehouse.
 */
public enum WarehouseType implements DomainCatalog {

    MARKETPLACE("MARKETPLACE", "Marketplace",
            "Warehouse owned and operated by the marketplace."),
    SELLER("SELLER", "Seller",
            "Warehouse owned and operated by an individual seller.");

    private final String code;
    private final String name;
    private final String description;

    WarehouseType(String code, String name, String description) {
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

    /** Resolves a warehouse type from its business code. */
    public static WarehouseType fromCode(String code) {
        for (WarehouseType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown warehouse type code: " + code);
    }
}