package nexusmarket.domain.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import nexusmarket.domain.valueobjects.UserRole;
import nexusmarket.domain.valueobjects.UserStatus;

/**
 * Represents a user responsible for registering and managing products on the marketplace.
 *
 * <p>Sellers cannot self-register; they are incorporated into the platform exclusively
 * by an {@link Administrator}, who is always referenced by the seller.</p>
 */
public class Seller extends User {

    private final Administrator registeredBy;
    private final List<Warehouse> warehouses = new ArrayList<>();
    private final List<Product> products = new ArrayList<>();

    public Seller(String userId, String fullName, String email, String identityDocument,
                  UserStatus status, Administrator registeredBy) {
        super(userId, fullName, email, identityDocument, UserRole.SELLER, status);
        this.registeredBy = Objects.requireNonNull(registeredBy,
                "A Seller cannot be created without an associated Administrator who performed the registration");
    }

    public Administrator getRegisteredBy() {
        return registeredBy;
    }

    public List<Warehouse> getWarehouses() {
        return Collections.unmodifiableList(warehouses);
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    /** Registers a warehouse owned by this seller. */
    public Warehouse openWarehouse(String warehouseId, String name, String location) {
        Warehouse warehouse = new Warehouse(warehouseId, name, location,
                nexusmarket.domain.valueobjects.WarehouseType.SELLER, this);
        warehouses.add(warehouse);
        return warehouse;
    }

    /** Associates an already-built warehouse with this seller. */
    public void addWarehouse(Warehouse warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse is required");
        }
        if (warehouse.getOwner() != this) {
            throw new IllegalArgumentException(
                    "Seller " + userId + " is not authorized to operate on warehouse "
                            + warehouse.getWarehouseId());
        }
        warehouses.add(warehouse);
    }

    /** Publishes a product owned by this seller. */
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product is required");
        }
        if (product.getSeller() != this) {
            throw new IllegalArgumentException(
                    "Seller " + userId + " is not authorized to operate on product "
                            + product.getProductId());
        }
        products.add(product);
    }

    public boolean ownsProduct(Product product) {
        return product != null && this.equals(product.getSeller());
    }

    public boolean ownsWarehouse(Warehouse warehouse) {
        return warehouse != null && this.equals(warehouse.getOwner());
    }
}