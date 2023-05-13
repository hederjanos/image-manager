package hu.ponte.hr.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Metadata class for data transfer of exceptions.
 */
@Getter
@Setter
@AllArgsConstructor
public class ApiError {

    /**
     * Type of error.
     */
    private String error;

    /**
     * Details of error.
     */
    private String details;

}
