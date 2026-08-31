package nexusmarket.domain.models;

import java.util.Objects;

/**
 * Represents a specific variation of a product, such as a color, size or model.
 *
 * <p>Value Object embedded within {@link Product}; it has no independent identity
 * outside the product it describes. Instances are immutable and compared by value.</p>
 */
public final class ProductVariant {

    private final String attributeName;
    private final String value;
    private final String sku;

    public ProductVariant(String attributeName, String value, String sku) {
        if (attributeName == null || attributeName.isBlank()) {
            throw new IllegalArgumentException("Variant attribute name is required");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Variant value is required");
        }
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("Variant SKU is required");
        }
        this.attributeName = attributeName;
        this.value = value;
        this.sku = sku;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getValue() {
        return value;
    }

    public String getSku() {
        return sku;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ProductVariant variant)) {
            return false;
        }
        return attributeName.equals(variant.attributeName)
                && value.equals(variant.value)
                && sku.equals(variant.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeName, value, sku);
    }

    @Override
    public String toString() {
        return "ProductVariant{attributeName='" + attributeName + "', value='" + value
                + "', sku='" + sku + "'}";
    }
}