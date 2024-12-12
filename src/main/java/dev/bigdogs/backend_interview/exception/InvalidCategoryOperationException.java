package dev.bigdogs.backend_interview.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a category operation violates a business rule or constraint.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCategoryOperationException extends RuntimeException {
    public InvalidCategoryOperationException() {
        super("Invalid category operation.");
    }

    public InvalidCategoryOperationException(String message) {
        super(message);
    }
}
