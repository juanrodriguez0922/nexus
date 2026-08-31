package nexusmarket.domain.models;

import nexusmarket.domain.valueobjects.UserRole;
import nexusmarket.domain.valueobjects.UserStatus;

/**
 * Represents a read-only, operational oversight profile used for monitoring and
 * reporting purposes. Supervisors do not create or modify commercial or operational
 * data; they consult consolidated administrative information.
 */
public class Supervisor extends User {

    public Supervisor(String userId, String fullName, String email,
                      String identityDocument, UserStatus status) {
        super(userId, fullName, email, identityDocument, UserRole.SUPERVISOR, status);
    }
}