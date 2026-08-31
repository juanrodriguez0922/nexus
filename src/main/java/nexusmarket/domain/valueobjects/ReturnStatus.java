package nexusmarket.domain.valueobjects;

/** Represents the current state of a buyer-initiated return request. */
public enum ReturnStatus implements DomainCatalog {

    REQUESTED("REQUESTED", "Requested", "Buyer has submitted a return request."),
    APPROVED("APPROVED", "Approved", "Seller or Administrator approved the return."),
    REJECTED("REJECTED", "Rejected", "Seller or Administrator rejected the return."),
    COMPLETED("COMPLETED", "Completed", "Returned product has been received and processed.");

    private final String code;
    private final String name;
    private final String description;

    ReturnStatus(String code, String name, String description) {
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

    /** Resolves a return status from its business code. */
    public static ReturnStatus fromCode(String code) {
        for (ReturnStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown return status code: " + code);
    }
}