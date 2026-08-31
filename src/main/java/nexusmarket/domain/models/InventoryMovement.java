package nexusmarket.domain.models;

import java.time.LocalDateTime;
import java.util.Objects;
import nexusmarket.domain.valueobjects.InventoryMovementType;

/**
 * Represents a significant event that changes the quantity of an inventory record.
 *
 * <p>{@code Inventory.availableQuantity} represents the current state, while an
 * {@code InventoryMovement} represents the event that caused a change to that state,
 * providing traceability.</p>
 */
public class InventoryMovement {

    private final String movementId;
    private final Inventory inventory;
    private final InventoryMovementType movementType;
    private final int quantity;
    private final LocalDateTime movementDate;
    private final User performedBy;

    public InventoryMovement(String movementId, Inventory inventory,
                             InventoryMovementType movementType, int quantity,
                             LocalDateTime movementDate, User performedBy) {
        Objects.requireNonNull(movementId, "Movement id is required");
        Objects.requireNonNull(inventory, "Movement inventory is required");
        Objects.requireNonNull(movementType, "Movement type is required");
        Objects.requireNonNull(movementDate, "Movement date is required");
        Objects.requireNonNull(performedBy, "Movement performer is required");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Movement quantity must be positive");
        }
        this.movementId = movementId;
        this.inventory = inventory;
        this.movementType = movementType;
        this.quantity = quantity;
        this.movementDate = movementDate;
        this.performedBy = performedBy;
    }

    public String getMovementId() {
        return movementId;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public InventoryMovementType getMovementType() {
        return movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getMovementDate() {
        return movementDate;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    @Override
    public String toString() {
        return "InventoryMovement{movementId='" + movementId + "', type=" + movementType
                + ", quantity=" + quantity + ", date=" + movementDate + "}";
    }
}