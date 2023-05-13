package hu.ponte.hr.exception.exceptions;

/**
 * Wrapper Exception class for file storage related operations.
 */
public class StorageException extends RuntimeException {

    public StorageException(Throwable cause) {
        super(cause);
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

}
