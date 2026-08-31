package nexusmarket.domain.valueobjects;

/**
 * Represents a buyer's current eligibility to place purchases on the marketplace.
 *
 * <p>{@code BuyerCommercialStatus} is independent from {@link UserStatus}. A buyer's
 * general account may be active while their commercial status restricts purchasing,
 * or vice versa, depending on business rules.</p>
 */
public enum BuyerCommercialStatus implements DomainCatalog {

    ENABLED("ENABLED", "Enabled", "Buyer is authorized to create carts and place orders."),
    RESTRICTED("RESTRICTED", "Restricted",
            "Buyer may browse the catalog but cannot complete purchases."),
    SUSPENDED("SUSPENDED", "Suspended", "Buyer's purchasing privileges have been revoked.");

    private final String code;
    private final String name;
    private final String description;

    BuyerCommercialStatus(String code, String name, String description) {
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

    /** Resolves a buyer commercial status from its business code. */
    public static BuyerCommercialStatus fromCode(String code) {
        for (BuyerCommercialStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown buyer commercial status code: " + code);
    }
}