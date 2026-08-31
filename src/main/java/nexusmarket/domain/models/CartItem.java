package nexusmarket.domain.models;

import java.util.Objects;

/**
 * Represents a single product selection within a shopping cart, including the
 * requested quantity and variant.
 *
 * <p>Value Object: instances are immutable and compared by value.</p>
 */
public final class CartItem {

    private final Product product;
    private final ProductVariant variant;
    private final int quantity;

    public CartItem(Product product, ProductVariant variant, int quantity) {
        Objects.requireNonNull(product, "Cart item product is required");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cart item quantity must be positive");
        }
        this.product = product;
        this.variant = variant;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public int getQuantity() {
        return quantity;
    }

    /** Line total at the product's current price. */
    public java.math.BigDecimal getLineTotal() {
        return product.getPrice().multiply(java.math.BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CartItem item)) {
            return false;
        }
        return product.equals(item.product)
                && Objects.equals(variant, item.variant)
                && quantity == item.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, variant, quantity);
    }

    @Override
    public String toString() {
        return "CartItem{product=" + product.getProductId() + ", variant=" + variant
                + ", quantity=" + quantity + "}";
    }
}