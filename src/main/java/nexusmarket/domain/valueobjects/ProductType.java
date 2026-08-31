package nexusmarket.domain.valueobjects;

/**
 * Represents the fulfillment classification of a product.
 *
 * <p>Physical products require inventory management and dispatch. Digital products
 * are delivered immediately after payment and do not require warehouse stock.</p>
 */
public enum ProductType implements DomainCatalog {

    PHYSICAL("PHYSICAL", "Physical",
            "Tangible good requiring inventory management and dispatch."),
    DIGITAL("DIGITAL", "Digital",
            "Intangible good delivered immediately after payment confirmation.");

    private final String code;
    private final String name;
    private final String description;

    ProductType(String code, String name, String description) {
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

    /** Resolves a product type from its business code. */
    public static ProductType fromCode(String code) {
        for (ProductType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown product type code: " + code);
    }
}