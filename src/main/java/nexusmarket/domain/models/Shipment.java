package nexusmarket.domain.models;

import java.time.LocalDateTime;
import java.util.Objects;
import nexusmarket.domain.valueobjects.ShipmentStatus;

/**
 * Represents the logistical process required to pack, dispatch and deliver the
 * physical products of an order.
 *
 * <p>A shipment fulfills exactly one order, departs from one warehouse and is
 * handled by one logistics operator.</p>
 */
public class Shipment {

    private final String shipmentId;
    private final Order order;
    private final Warehouse originWarehouse;
    private final LogisticsOperator handledBy;
    private ShipmentStatus shipmentStatus;
    private final LocalDateTime dispatchDate;
    private LocalDateTime deliveryDate;

    public Shipment(String shipmentId, Order order, Warehouse originWarehouse,
                    LogisticsOperator handledBy, LocalDateTime dispatchDate) {
        Objects.requireNonNull(shipmentId, "Shipment id is required");
        Objects.requireNonNull(order, "Shipment order is required");
        Objects.requireNonNull(originWarehouse, "Shipment origin warehouse is required");
        Objects.requireNonNull(handledBy, "Shipment logistics operator is required");
        Objects.requireNonNull(dispatchDate, "Shipment dispatch date is required");
        this.shipmentId = shipmentId;
        this.order = order;
        this.originWarehouse = originWarehouse;
        this.handledBy = handledBy;
        this.dispatchDate = dispatchDate;
        this.shipmentStatus = ShipmentStatus.IN_TRANSIT;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public Order getOrder() {
        return order;
    }

    public Warehouse getOriginWarehouse() {
        return originWarehouse;
    }

    public LogisticsOperator getHandledBy() {
        return handledBy;
    }

    public ShipmentStatus getShipmentStatus() {
        return shipmentStatus;
    }

    public LocalDateTime getDispatchDate() {
        return dispatchDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    /** Confirms the delivery of the shipment. */
    public void markDelivered(LocalDateTime deliveryDate) {
        Objects.requireNonNull(deliveryDate, "Delivery date is required");
        if (shipmentStatus == ShipmentStatus.DELIVERED) {
            return;
        }
        if (shipmentStatus == ShipmentStatus.FAILED) {
            throw new IllegalStateException(
                    "A failed shipment must be resolved before being delivered");
        }
        this.shipmentStatus = ShipmentStatus.DELIVERED;
        this.deliveryDate = deliveryDate;
    }

    /** Marks the delivery attempt as failed; requires resolution. */
    public void markFailed() {
        if (shipmentStatus == ShipmentStatus.DELIVERED) {
            throw new IllegalStateException("A delivered shipment cannot fail");
        }
        this.shipmentStatus = ShipmentStatus.FAILED;
    }

    /** Recovers a failed shipment back to in transit. */
    public void resume() {
        if (shipmentStatus != ShipmentStatus.FAILED) {
            throw new IllegalStateException("Only failed shipments can be resumed");
        }
        this.shipmentStatus = ShipmentStatus.IN_TRANSIT;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Shipment shipment)) {
            return false;
        }
        return shipmentId.equals(shipment.shipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipmentId);
    }

    @Override
    public String toString() {
        return "Shipment{shipmentId='" + shipmentId + "', order=" + order.getOrderId()
                + ", status=" + shipmentStatus + "}";
    }
}