package nexusmarket.domain.models;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a single product line within a confirmed order, preserving the price
 * and quantity at the moment of purchase.
 *
 * <p>Value Object: instances are immutable and compared by value.</p>
 */
public final class OrderItem {

    private final Product product;
    private final ProductVariant variant;
    private final int quantity;
    private final BigDecimal unitPrice;

    public OrderItem(Product product, ProductVariant variant, int quantity, BigDecimal unitPrice) {
        Objects.requireNonNull(product, "Order item product is required");
        Objects.requireNonNull(unitPrice, "Order item unit price is required");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Order item quantity must be positive");
        }
        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Order item unit price must be non-negative");
        }
        this.product = product;
        this.variant = variant;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /** Builds an order line from a cart selection, freezing the current price. */
    public static OrderItem fromCartItem(CartItem cartItem) {
        Objects.requireNonNull(cartItem, "Cart item is required");
        return new OrderItem(cartItem.getProduct(), cartItem.getVariant(),
                cartItem.getQuantity(), cartItem.getProduct().getPrice());
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OrderItem item)) {
            return false;
        }
        return product.equals(item.product)
                && Objects.equals(variant, item.variant)
                && quantity == item.quantity
                && unitPrice.compareTo(item.unitPrice) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, variant, quantity, unitPrice);
    }

    @Override
    public String toString() {
        return "OrderItem{product=" + product.getProductId() + ", quantity=" + quantity
                + ", unitPrice=" + unitPrice + "}";
    }
}