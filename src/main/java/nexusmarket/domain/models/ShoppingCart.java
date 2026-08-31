package nexusmarket.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents the provisional, pre-purchase selection of products made by a buyer.
 *
 * <p>A shopping cart is converted into an {@link Order} once the buyer confirms
 * the purchase.</p>
 */
public class ShoppingCart {

    private final String cartId;
    private final Buyer buyer;
    private final List<CartItem> items = new ArrayList<>();
    private final LocalDateTime createdAt;

    public ShoppingCart(String cartId, Buyer buyer) {
        this(cartId, buyer, LocalDateTime.now());
    }

    public ShoppingCart(String cartId, Buyer buyer, LocalDateTime createdAt) {
        Objects.requireNonNull(cartId, "Cart id is required");
        Objects.requireNonNull(buyer, "Cart buyer is required");
        Objects.requireNonNull(createdAt, "Cart creation date is required");
        this.cartId = cartId;
        this.buyer = buyer;
        this.createdAt = createdAt;
    }

    public String getCartId() {
        return cartId;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** Adds a product selection to the cart. */
    public void addItem(Product product, ProductVariant variant, int quantity) {
        Objects.requireNonNull(product, "Product is required");
        product.ensureAvailable();
        items.add(new CartItem(product, variant, quantity));
    }

    /** Removes every selection of the given product from the cart. */
    public void removeItem(Product product) {
        Objects.requireNonNull(product, "Product is required");
        items.removeIf(item -> item.getProduct().equals(product));
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** Total amount of the cart at current product prices. */
    public BigDecimal calculateTotal() {
        return items.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Converts the cart into a confirmed order. The cart is emptied afterwards,
     * mirroring the lifecycle Cart → PendingPayment.
     */
    public Order checkout() {
        buyer.ensureEligibleToPurchase();
        if (items.isEmpty()) {
            throw new IllegalStateException("An empty cart cannot be converted into an order");
        }
        Order order = new Order(UUID.randomUUID().toString(), buyer,
                new ArrayList<>(items), LocalDateTime.now());
        clear();
        return order;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ShoppingCart cart)) {
            return false;
        }
        return cartId.equals(cart.cartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId);
    }

    @Override
    public String toString() {
        return "ShoppingCart{cartId='" + cartId + "', buyer=" + buyer.getUserId()
                + ", items=" + items.size() + "}";
    }
}