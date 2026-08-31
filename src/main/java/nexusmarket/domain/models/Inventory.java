package nexusmarket.domain.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import nexusmarket.domain.valueobjects.InventoryMovementType;

/**
 * Represents distributed stock of a physical product, always linked to exactly one
 * product and one warehouse.
 *
 * <p>Inventory quantities must never become negative under any circumstance, and
 * inventory cannot be reserved when it is insufficient or marked as damaged.</p>
 */
public class Inventory {

    /** Operational state of the stock held in a warehouse. */
    public enum StockState {
        AVAILABLE,
        DAMAGED
    }

    private final String inventoryId;
    private final Product product;
    private final Warehouse warehouse;
    private int availableQuantity;
    private int reservedQuantity;
    private StockState state = StockState.AVAILABLE;
    private final List<InventoryMovement> movements = new ArrayList<>();

    public Inventory(String inventoryId, Product product, Warehouse warehouse,
                     int availableQuantity, int reservedQuantity) {
        Objects.requireNonNull(inventoryId, "Inventory id is required");
        Objects.requireNonNull(product, "Inventory product is required");
        Objects.requireNonNull(warehouse, "Inventory warehouse is required");
        if (!product.isPhysical()) {
            throw new IllegalArgumentException(
                    "Only physical products require inventory: " + product.getProductId());
        }
        this.inventoryId = inventoryId;
        this.product = product;
        this.warehouse = warehouse;
        setAvailableQuantity(availableQuantity);
        setReservedQuantity(reservedQuantity);
        warehouse.addInventory(this);
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public Product getProduct() {
        return product;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public StockState getState() {
        return state;
    }

    public void markDamaged() {
        this.state = StockState.DAMAGED;
    }

    public void markAvailable() {
        this.state = StockState.AVAILABLE;
    }

    public List<InventoryMovement> getMovements() {
        return Collections.unmodifiableList(movements);
    }

    private void setAvailableQuantity(int availableQuantity) {
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("Inventory available quantity must never be negative");
        }
        this.availableQuantity = availableQuantity;
    }

    private void setReservedQuantity(int reservedQuantity) {
        if (reservedQuantity < 0) {
            throw new IllegalArgumentException("Inventory reserved quantity must never be negative");
        }
        this.reservedQuantity = reservedQuantity;
    }

    /** Registers a movement providing traceability for a change of state. */
    private void recordMovement(InventoryMovementType type, int quantity, User performedBy) {
        movements.add(new InventoryMovement(
                java.util.UUID.randomUUID().toString(), this, type, quantity,
                java.time.LocalDateTime.now(), performedBy));
    }

    /** Entry of new stock into the warehouse. */
    public void inbound(int quantity, User performedBy) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Inbound quantity must be positive");
        }
        availableQuantity += quantity;
        recordMovement(InventoryMovementType.INBOUND, quantity, performedBy);
    }

    /**
     * Temporarily holds stock for a pending order. Rejected when the stock is
     * insufficient or the inventory is marked as damaged.
     */
    public void reserve(int quantity, User performedBy) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Reservation quantity must be positive");
        }
        if (state == StockState.DAMAGED) {
            throw new IllegalStateException(
                    "Inventory " + inventoryId + " is marked as damaged and cannot be reserved");
        }
        if (availableQuantity < quantity) {
            throw new IllegalStateException(
                    "Insufficient stock for product " + product.getProductId() + ": requested "
                            + quantity + ", available " + availableQuantity);
        }
        availableQuantity -= quantity;
        reservedQuantity += quantity;
        recordMovement(InventoryMovementType.RESERVATION, quantity, performedBy);
    }

    /** Releases a previous reservation, returning stock to available. */
    public void releaseReservation(int quantity, User performedBy) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Release quantity must be positive");
        }
        if (reservedQuantity < quantity) {
            throw new IllegalArgumentException(
                    "Cannot release more than the reserved quantity");
        }
        reservedQuantity -= quantity;
        availableQuantity += quantity;
        recordMovement(InventoryMovementType.ADJUSTMENT, quantity, performedBy);
    }

    /** Removes reserved stock due to a completed sale (dispatch). */
    public void confirmOutbound(int quantity, User performedBy) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Outbound quantity must be positive");
        }
        if (reservedQuantity < quantity) {
            throw new IllegalStateException(
                    "Inventory " + inventoryId + " has " + reservedQuantity
                            + " reserved units but " + quantity + " were requested for outbound");
        }
        reservedQuantity -= quantity;
        recordMovement(InventoryMovementType.OUTBOUND, quantity, performedBy);
    }

    /** Manual correction of recorded stock. */
    public void adjust(int newAvailableQuantity, User performedBy) {
        setAvailableQuantity(newAvailableQuantity);
        recordMovement(InventoryMovementType.ADJUSTMENT, newAvailableQuantity, performedBy);
    }

    /** Reintroduction of stock resulting from an approved return. */
    public void restock(int quantity, User performedBy) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }
        availableQuantity += quantity;
        recordMovement(InventoryMovementType.RETURN, quantity, performedBy);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Inventory inventory)) {
            return false;
        }
        return inventoryId.equals(inventory.inventoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryId);
    }

    @Override
    public String toString() {
        return "Inventory{inventoryId='" + inventoryId + "', available=" + availableQuantity
                + ", reserved=" + reservedQuantity + ", state=" + state + "}";
    }
}