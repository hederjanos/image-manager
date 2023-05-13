package hu.ponte.hr.exception.exceptions;

/**
 * Wrapper Exception class for {@link java.security.SignatureException}.
 */
public class CustomSignatureException extends RuntimeException {

    public CustomSignatureException(String message) {
        super(message);
    }

    public CustomSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

}
