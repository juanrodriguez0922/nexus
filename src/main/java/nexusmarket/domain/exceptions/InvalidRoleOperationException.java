package nexusmarket.domain.exceptions;

/**
 * Thrown when a user attempts to perform an operation that is outside the
 * scope of their assigned role or current status.
 */
public class InvalidRoleOperationException extends RuntimeException {

    public InvalidRoleOperationException(String message) {
        super(message);
    }
}