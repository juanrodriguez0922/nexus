package nexusmarket.domain.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nexusmarket.domain.exceptions.BuyerNotEligibleException;
import nexusmarket.domain.valueobjects.BuyerCommercialStatus;
import nexusmarket.domain.valueobjects.UserRole;
import nexusmarket.domain.valueobjects.UserStatus;

/**
 * Represents a user who purchases products published on the marketplace.
 *
 * <p>A buyer may register multiple delivery addresses and maintains a commercial
 * status independent of their general account status. A buyer owns exactly one
 * active shopping cart (created on demand) and places zero or more orders.</p>
 */
public class Buyer extends User {

    private String primaryAddress;
    private final List<String> additionalAddresses = new ArrayList<>();
    private BuyerCommercialStatus commercialStatus;
    private ShoppingCart cart;
    private final List<Order> orders = new ArrayList<>();

    public Buyer(String userId, String fullName, String email, String identityDocument,
                 String primaryAddress, UserStatus status, BuyerCommercialStatus commercialStatus) {
        super(userId, fullName, email, identityDocument, UserRole.BUYER, status);
        this.primaryAddress = primaryAddress;
        this.commercialStatus = commercialStatus == null
                ? BuyerCommercialStatus.ENABLED
                : commercialStatus;
    }

    public String getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(String primaryAddress) {
        requireText(primaryAddress, "Primary address");
        this.primaryAddress = primaryAddress;
    }

    public List<String> getAdditionalAddresses() {
        return Collections.unmodifiableList(additionalAddresses);
    }

    public void addAdditionalAddress(String address) {
        requireText(address, "Additional address");
        additionalAddresses.add(address);
    }

    public BuyerCommercialStatus getCommercialStatus() {
        return commercialStatus;
    }

    public void setCommercialStatus(BuyerCommercialStatus commercialStatus) {
        this.commercialStatus = commercialStatus == null
                ? BuyerCommercialStatus.ENABLED
                : commercialStatus;
    }

    /** Returns the buyer's active cart, creating it on demand. */
    public ShoppingCart getOrCreateCart() {
        if (cart == null) {
            cart = new ShoppingCart(java.util.UUID.randomUUID().toString(), this);
        }
        return cart;
    }

    /** Re-attaches a persisted cart (used by persistence adapters). */
    public void setCart(ShoppingCart cart) {
        this.cart = cart;
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    /** Registers an order placed by this buyer. */
    public void registerOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order is required");
        }
        orders.add(order);
    }

    /** A buyer can purchase only when the account is active and commercially enabled. */
    public boolean isEligibleToPurchase() {
        return status == UserStatus.ACTIVE && commercialStatus == BuyerCommercialStatus.ENABLED;
    }

    /** Ensures the buyer is authorized to create carts and place orders. */
    public void ensureEligibleToPurchase() {
        if (!isEligibleToPurchase()) {
            throw new BuyerNotEligibleException(userId, commercialStatus.getName());
        }
    }
}