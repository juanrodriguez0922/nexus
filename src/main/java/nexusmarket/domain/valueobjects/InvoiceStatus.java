package nexusmarket.domain.valueobjects;

/** Represents the current state of an invoice generated for a paid order. */
public enum InvoiceStatus implements DomainCatalog {

    ISSUED("ISSUED", "Issued", "Invoice has been generated and delivered to the buyer."),
    CANCELLED("CANCELLED", "Cancelled", "Invoice was cancelled, typically due to an order issue.");

    private final String code;
    private final String name;
    private final String description;

    InvoiceStatus(String code, String name, String description) {
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

    /** Resolves an invoice status from its business code. */
    public static InvoiceStatus fromCode(String code) {
        for (InvoiceStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown invoice status code: " + code);
    }
}