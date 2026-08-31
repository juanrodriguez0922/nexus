package nexusmarket.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import nexusmarket.domain.valueobjects.RefundStatus;

/**
 * Represents the monetary reimbursement issued to a buyer as a result of an
 * approved return. A refund originates from exactly one return.
 */
public class Refund {

    private final String refundId;
    private final Return relatedReturn;
    private final BigDecimal amount;
    private RefundStatus refundStatus;
    private LocalDateTime processedAt;

    public Refund(String refundId, Return relatedReturn, BigDecimal amount) {
        Objects.requireNonNull(refundId, "Refund id is required");
        Objects.requireNonNull(relatedReturn, "Refund related return is required");
        Objects.requireNonNull(amount, "Refund amount is required");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }
        this.refundId = refundId;
        this.relatedReturn = relatedReturn;
        this.amount = amount;
        this.refundStatus = RefundStatus.REQUESTED;
    }

    public String getRefundId() {
        return refundId;
    }

    public Return getRelatedReturn() {
        return relatedReturn;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public RefundStatus getRefundStatus() {
        return refundStatus;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    /** Authorizes the refund (by Seller or Administrator). */
    public void approve() {
        if (refundStatus != RefundStatus.REQUESTED) {
            throw new IllegalStateException("Only requested refunds can be approved");
        }
        this.refundStatus = RefundStatus.APPROVED;
    }

    /** Denies the refund request. */
    public void reject() {
        if (refundStatus != RefundStatus.REQUESTED) {
            throw new IllegalStateException("Only requested refunds can be rejected");
        }
        this.refundStatus = RefundStatus.REJECTED;
    }

    /** Marks the funds as returned to the buyer. */
    public void markProcessed(LocalDateTime processedAt) {
        if (refundStatus != RefundStatus.APPROVED) {
            throw new IllegalStateException("Only approved refunds can be processed");
        }
        this.refundStatus = RefundStatus.PROCESSED;
        this.processedAt = processedAt;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Refund refund)) {
            return false;
        }
        return refundId.equals(refund.refundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refundId);
    }

    @Override
    public String toString() {
        return "Refund{refundId='" + refundId + "', amount=" + amount
                + ", status=" + refundStatus + "}";
    }
}