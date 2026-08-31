package nexusmarket.domain.models;

import java.time.LocalDateTime;
import java.util.Objects;
import nexusmarket.domain.valueobjects.ReturnStatus;

/**
 * Represents a buyer-initiated request to return one or more products from a
 * delivered order. A return may generate zero or one refund.
 */
public class Return {

    private final String returnId;
    private final Order order;
    private final String reason;
    private ReturnStatus returnStatus;
    private final LocalDateTime requestedAt;
    private Refund refund;

    public Return(String returnId, Order order, String reason, LocalDateTime requestedAt) {
        Objects.requireNonNull(returnId, "Return id is required");
        Objects.requireNonNull(order, "Return order is required");
        Objects.requireNonNull(reason, "Return reason is required");
        Objects.requireNonNull(requestedAt, "Return request date is required");
        this.returnId = returnId;
        this.order = order;
        this.reason = reason;
        this.returnStatus = ReturnStatus.REQUESTED;
        this.requestedAt = requestedAt;
    }

    public String getReturnId() {
        return returnId;
    }

    public Order getOrder() {
        return order;
    }

    public String getReason() {
        return reason;
    }

    public ReturnStatus getReturnStatus() {
        return returnStatus;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public Refund getRefund() {
        return refund;
    }

    /** Approves the return (by Seller or Administrator). */
    public void approve() {
        if (returnStatus != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("Only requested returns can be approved");
        }
        this.returnStatus = ReturnStatus.APPROVED;
    }

    /** Rejects the return (by Seller or Administrator). */
    public void reject() {
        if (returnStatus != ReturnStatus.REQUESTED) {
            throw new IllegalStateException("Only requested returns can be rejected");
        }
        this.returnStatus = ReturnStatus.REJECTED;
    }

    /** Marks the returned product as received and processed. */
    public void complete() {
        if (returnStatus != ReturnStatus.APPROVED) {
            throw new IllegalStateException("Only approved returns can be completed");
        }
        this.returnStatus = ReturnStatus.COMPLETED;
    }

    /** Generates the refund associated with this approved return. */
    public Refund generateRefund(java.math.BigDecimal amount) {
        if (returnStatus != ReturnStatus.APPROVED && returnStatus != ReturnStatus.COMPLETED) {
            throw new IllegalStateException(
                    "A refund can only originate from an approved return");
        }
        if (refund != null) {
            throw new IllegalStateException("Return " + returnId + " already has a refund");
        }
        this.refund = new Refund(java.util.UUID.randomUUID().toString(), this, amount);
        return refund;
    }

    /** Re-attaches a persisted refund (used by persistence adapters). */
    public void attachRefund(Refund refund) {
        this.refund = refund;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Return requested)) {
            return false;
        }
        return returnId.equals(requested.returnId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnId);
    }

    @Override
    public String toString() {
        return "Return{returnId='" + returnId + "', order=" + order.getOrderId()
                + ", status=" + returnStatus + "}";
    }
}