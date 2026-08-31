package nexusmarket.domain.valueobjects;

/**
 * Represents the single responsibility and permission set assigned to a user
 * within the marketplace. Each user has exactly one role, and a user must never
 * manage information outside the scope of that role.
 */
public enum UserRole implements DomainCatalog {

    BUYER("BUYER", "Buyer", "User who purchases published products."),
    SELLER("SELLER", "Seller", "User responsible for registering and administering their products."),
    LOGISTICS_OPERATOR("LOGISTICS_OPERATOR", "Logistics Operator",
            "User responsible for the physical operation of warehouses and dispatch."),
    ADMINISTRATOR("ADMINISTRATOR", "Administrator",
            "User responsible for administering sellers and warehouses."),
    SUPERVISOR("SUPERVISOR", "Supervisor",
            "Read-only profile for operational consultation and follow-up.");

    private final String code;
    private final String name;
    private final String description;

    UserRole(String code, String name, String description) {
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

    /** Resolves a user role from its business code. */
    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equalsIgnoreCase(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown user role code: " + code);
    }
}