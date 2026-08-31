package nexusmarket.domain.valueobjects;

/** Represents the current state of a monetary reimbursement associated with an approved return. */
public enum RefundStatus implements DomainCatalog {

    REQUESTED("REQUESTED", "Requested", "Refund has been requested following an approved return."),
    APPROVED("APPROVED", "Approved", "Refund has been authorized by Seller or Administrator."),
    PROCESSED("PROCESSED", "Processed", "Funds have been returned to the buyer."),
    REJECTED("REJECTED", "Rejected", "Refund request has been denied.");

    private final String code;
    private final String name;
    private final String description;

    RefundStatus(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /** Resolves a refund status from its business code. */
    public static RefundStatus fromCode(String code) {
        for (RefundStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown refund status code: " + code);
    }
}