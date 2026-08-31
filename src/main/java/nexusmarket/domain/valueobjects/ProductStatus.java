package nexusmarket.domain.valueobjects;

/**
 * Represents the current publication state of a product within the catalog.
 */
public enum ProductStatus implements DomainCatalog {

    PUBLISHED("PUBLISHED", "Published", "Product is visible and available in the public catalog."),
    SUSPENDED("SUSPENDED", "Suspended", "Product is temporarily hidden from the public catalog."),
    DISCONTINUED("DISCONTINUED", "Discontinued", "Product is permanently retired from the catalog.");

    private final String code;
    private final String name;
    private final String description;

    ProductStatus(String code, String name, String description) {
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

    /** Resolves a product status from its business code. */
    public static ProductStatus fromCode(String code) {
        for (ProductStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown product status code: " + code);
    }
}