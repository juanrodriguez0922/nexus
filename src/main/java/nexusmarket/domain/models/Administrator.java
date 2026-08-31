package nexusmarket.domain.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import nexusmarket.domain.valueobjects.UserRole;
import nexusmarket.domain.valueobjects.UserStatus;
import nexusmarket.domain.valueobjects.WarehouseType;

/**
 * Represents a user responsible for onboarding sellers and administering warehouses.
 *
 * <p>An administrator registers sellers (the only way a seller can join the platform)
 * and may register Marketplace-owned warehouses.</p>
 */
public class Administrator extends User {

    private final List<Seller> registeredSellers = new ArrayList<>();
    private final List<Warehouse> registeredWarehouses = new ArrayList<>();

    public Administrator(String userId, String fullName, String email,
                         String identityDocument, UserStatus status) {
        super(userId, fullName, email, identityDocument, UserRole.ADMINISTRATOR, status);
    }

    public List<Seller> getRegisteredSellers() {
        return Collections.unmodifiableList(registeredSellers);
    }

    /**
     * Incorporates a new seller into the platform. Sellers cannot self-register.
     */
    public Seller registerSeller(String userId, String fullName, String email,
                                 String identityDocument, UserStatus status) {
        Seller seller = new Seller(userId, fullName, email, identityDocument, status, this);
        registeredSellers.add(seller);
        return seller;
    }

    /** Registers a Marketplace-owned warehouse (no seller owner). */
    public Warehouse registerMarketplaceWarehouse(String warehouseId, String name, String location) {
        Warehouse warehouse = new Warehouse(warehouseId, name, location, WarehouseType.MARKETPLACE, null);
        registeredWarehouses.add(warehouse);
        return warehouse;
    }

    /** Associates an already-built warehouse with this administrator. */
    public void addWarehouse(Warehouse warehouse) {
        Objects.requireNonNull(warehouse, "Warehouse is required");
        registeredWarehouses.add(warehouse);
    }

    public List<Warehouse> getRegisteredWarehouses() {
        return Collections.unmodifiableList(registeredWarehouses);
    }
}