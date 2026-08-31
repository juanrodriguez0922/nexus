package nexusmarket.domain.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nexusmarket.domain.valueobjects.UserRole;
import nexusmarket.domain.valueobjects.UserStatus;

/**
 * Represents a user responsible for the physical operation of warehouses and
 * the dispatch of orders. An operator may be assigned to zero or more warehouses,
 * regardless of ownership.
 */
public class LogisticsOperator extends User {

    private final List<Warehouse> assignedWarehouses = new ArrayList<>();

    public LogisticsOperator(String userId, String fullName, String email,
                             String identityDocument, UserStatus status) {
        super(userId, fullName, email, identityDocument, UserRole.LOGISTICS_OPERATOR, status);
    }

    public List<Warehouse> getAssignedWarehouses() {
        return Collections.unmodifiableList(assignedWarehouses);
    }

    /** Associates a warehouse with this operator (coordinated by {@code Warehouse.assignOperator}). */
    void addAssignedWarehouse(Warehouse warehouse) {
        if (warehouse != null && !assignedWarehouses.contains(warehouse)) {
            assignedWarehouses.add(warehouse);
        }
    }

    /** Ensures the operator is responsible for the given warehouse. */
    public void ensureAssignedTo(Warehouse warehouse) {
        if (warehouse == null || !assignedWarehouses.contains(warehouse)) {
            throw new nexusmarket.domain.exceptions.InvalidRoleOperationException(
                    "Logistics operator " + userId + " is not assigned to warehouse "
                            + (warehouse == null ? "?" : warehouse.getWarehouseId()));
        }
    }
}