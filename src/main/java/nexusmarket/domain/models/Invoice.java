package nexusmarket.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import nexusmarket.domain.valueobjects.InvoiceStatus;

/**
 * Represents the commercial billing information associated with a paid order.
 * An invoice is generated from exactly one order.
 */
public class Invoice {

    private final String invoiceId;
    private final Order order;
    private final LocalDateTime issueDate;
    private final BigDecimal totalAmount;
    private InvoiceStatus invoiceStatus;

    public Invoice(String invoiceId, Order order, LocalDateTime issueDate) {
        Objects.requireNonNull(invoiceId, "Invoice id is required");
        Objects.requireNonNull(order, "Invoice order is required");
        Objects.requireNonNull(issueDate, "Invoice issue date is required");
        this.invoiceId = invoiceId;
        this.order = order;
        this.issueDate = issueDate;
        this.totalAmount = order.getTotalAmount();
        this.invoiceStatus = InvoiceStatus.ISSUED;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public Order getOrder() {
        return order;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public InvoiceStatus getInvoiceStatus() {
        return invoiceStatus;
    }

    /** Cancels the invoice, typically due to an order issue. */
    public void cancel() {
        this.invoiceStatus = InvoiceStatus.CANCELLED;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Invoice invoice)) {
            return false;
        }
        return invoiceId.equals(invoice.invoiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId);
    }

    @Override
    public String toString() {
        return "Invoice{invoiceId='" + invoiceId + "', order=" + order.getOrderId()
                + ", total=" + totalAmount + ", status=" + invoiceStatus + "}";
    }
}