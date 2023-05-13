package hu.ponte.hr.exception;

import hu.ponte.hr.exception.exceptions.CustomSignatureException;
import hu.ponte.hr.exception.exceptions.MultipartFileException;
import hu.ponte.hr.exception.exceptions.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

/**
 * Global exception handler class.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error(ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError body = new ApiError("CONSTRAINT_VALIDATION_ERROR", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error(ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError body = new ApiError("MAX_UPLOAD_SIZE_EXCEEDED_ERROR", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(MultipartFileException.class)
    public ResponseEntity<ApiError> handleStorageException(MultipartFileException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError body = new ApiError("MULTIPART_FILE_ERROR", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ApiError> handleStorageException(StorageException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError body = new ApiError("STORAGE_ERROR", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(CustomSignatureException.class)
    public ResponseEntity<ApiError> handleCustomSignatureException(CustomSignatureException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError body = new ApiError("SIGNATURE_ERROR", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError body = new ApiError("RESOURCE_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> defaultErrorHandler(Throwable t) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError body = new ApiError("UNEXPECTED_ERROR", "Something unexpected occurred: " + t.getMessage());
        return new ResponseEntity<>(body, status);
    }

}

