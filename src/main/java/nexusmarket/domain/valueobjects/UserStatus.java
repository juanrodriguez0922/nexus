package nexusmarket.domain.valueobjects;

/**
 * Represents the current operational status of a user's access to the marketplace.
 */
public enum UserStatus implements DomainCatalog {

    ACTIVE("ACTIVE", "Active", "User can access and operate within the system normally."),
    INACTIVE("INACTIVE", "Inactive", "User exists but is not currently enabled for operations."),
    BLOCKED("BLOCKED", "Blocked", "User access has been suspended.");

    private final String code;
    private final String name;
    private final String description;

    UserStatus(String code, String name, String description) {
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

    /** Resolves a user status from its business code. */
    public static UserStatus fromCode(String code) {
        for (UserStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown user status code: " + code);
    }
}