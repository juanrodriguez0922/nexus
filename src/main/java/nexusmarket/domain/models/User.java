package nexusmarket.domain.models;

import java.util.Objects;
import nexusmarket.domain.valueobjects.UserRole;
import nexusmarket.domain.valueobjects.UserStatus;

/**
 * Represents any person authorized to interact with the NexusMarket system.
 *
 * <p>This abstract class centralizes the identity, contact and access information
 * shared by all marketplace participants. The role assigned to a user determines
 * their responsibilities within the system, and a user must never manage
 * information outside the scope of that role.</p>
 *
 * <p>This class cannot be instantiated directly.</p>
 */
public abstract class User {

    protected final String userId;
    protected String fullName;
    protected String email;
    protected final String identityDocument;
    protected final UserRole role;
    protected UserStatus status;

    protected User(String userId, String fullName, String email, String identityDocument,
                   UserRole role, UserStatus status) {
        requireText(userId, "User id");
        requireText(fullName, "Full name");
        requireText(email, "Email");
        requireText(identityDocument, "Identity document");
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.identityDocument = identityDocument;
        this.role = Objects.requireNonNull(role, "Role is required");
        this.status = status == null ? UserStatus.ACTIVE : status;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getIdentityDocument() {
        return identityDocument;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setFullName(String fullName) {
        requireText(fullName, "Full name");
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        requireText(email, "Email");
        this.email = email;
    }

    public void setStatus(UserStatus status) {
        this.status = status == null ? UserStatus.ACTIVE : status;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void block() {
        this.status = UserStatus.BLOCKED;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    /** Ensures the user is enabled to operate within the system. */
    public void ensureActive() {
        if (status != UserStatus.ACTIVE) {
            throw new nexusmarket.domain.exceptions.InvalidRoleOperationException(
                    "User " + userId + " is not active (status: " + status.getName() + ")");
        }
    }

    protected static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof User user)) {
            return false;
        }
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{userId='" + userId + "', fullName='" + fullName
                + "', role=" + role + ", status=" + status + "}";
    }
}