package nexusmarket.domain.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import nexusmarket.domain.valueobjects.WarehouseType;

/**
 * Represents a physical location used to store and manage inventory.
 *
 * <p>Warehouses are classified as either Marketplace-owned or Seller-owned.
 * Marketplace warehouses have no seller owner.</p>
 */
public class Warehouse {

    private final String warehouseId;
    private String name;
    private String location;
    private final WarehouseType warehouseType;
    private final Seller owner;
    private final List<Inventory> inventories = new ArrayList<>();
    private final List<LogisticsOperator> operators = new ArrayList<>();

    public Warehouse(String warehouseId, String name, String location,
                     WarehouseType warehouseType, Seller owner) {
        Objects.requireNonNull(warehouseId, "Warehouse id is required");
        Objects.requireNonNull(name, "Warehouse name is required");
        Objects.requireNonNull(location, "Warehouse location is required");
        this.warehouseId = warehouseId;
        this.name = name;
        this.location = location;
        this.warehouseType = warehouseType == null
                ? (owner == null ? WarehouseType.MARKETPLACE : WarehouseType.SELLER)
                : warehouseType;
        this.owner = owner;
        validateOwnershipConsistency();
    }

    private void validateOwnershipConsistency() {
        if (owner == null && this.warehouseType != WarehouseType.MARKETPLACE) {
            throw new IllegalArgumentException(
                    "A warehouse without seller owner must be of type MARKETPLACE");
        }
        if (owner != null && this.warehouseType != WarehouseType.SELLER) {
            throw new IllegalArgumentException(
                    "A seller-owned warehouse must be of type SELLER");
        }
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Objects.requireNonNull(name, "Warehouse name is required");
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        Objects.requireNonNull(location, "Warehouse location is required");
        this.location = location;
    }

    public WarehouseType getWarehouseType() {
        return warehouseType;
    }

    /** Null for Marketplace warehouses. */
    public Seller getOwner() {
        return owner;
    }

    public List<Inventory> getInventories() {
        return Collections.unmodifiableList(inventories);
    }

    /** Registers an inventory record held by this warehouse. */
    public void addInventory(Inventory inventory) {
        Objects.requireNonNull(inventory, "Inventory is required");
        if (inventory.getWarehouse() != this) {
            throw new IllegalArgumentException(
                    "Inventory does not reference this warehouse");
        }
        inventories.add(inventory);
    }

    public List<LogisticsOperator> getOperators() {
        return Collections.unmodifiableList(operators);
    }

    /** Assigns a logistics operator to this warehouse, keeping both sides consistent. */
    public void assignOperator(LogisticsOperator operator) {
        Objects.requireNonNull(operator, "Logistics operator is required");
        if (!operators.contains(operator)) {
            operators.add(operator);
        }
        operator.addAssignedWarehouse(this);
    }

    public boolean isMarketplaceOwned() {
        return owner == null;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Warehouse warehouse)) {
            return false;
        }
        return warehouseId.equals(warehouse.warehouseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warehouseId);
    }

    @Override
    public String toString() {
        return "Warehouse{warehouseId='" + warehouseId + "', name='" + name
                + "', type=" + warehouseType + "}";
    }
}