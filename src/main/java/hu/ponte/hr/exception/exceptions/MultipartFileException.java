package hu.ponte.hr.exception.exceptions;

/**
 * Wrapper Exception class for {@link org.springframework.web.multipart.MultipartFile} related operations.
 */
public class MultipartFileException extends RuntimeException {

    public MultipartFileException(String message) {
        super(message);
    }

    public MultipartFileException(String message, Throwable cause) {
        super(message, cause);
    }

}
