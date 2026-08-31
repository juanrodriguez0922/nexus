package nexusmarket.domain.valueobjects;

/**
 * Represents a generic business catalog used throughout the NexusMarket domain.
 *
 * <p>{@code DomainCatalog} provides a consistent structure for controlled business
 * values that require a code, human-readable name, and business description.</p>
 *
 * <p>This interface cannot be instantiated directly; it is implemented by domain
 * catalog enumerations such as {@link UserRole}, {@link UserStatus},
 * {@link ProductStatus}, and others.</p>
 */
public interface DomainCatalog {

    /** Unique business identifier of the catalog value. */
    String getCode();

    /** Human-readable name displayed within the application. */
    String getName();

    /** Business definition of the catalog value. */
    String getDescription();
}