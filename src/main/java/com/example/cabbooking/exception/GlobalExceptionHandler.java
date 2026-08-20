package com.example.cabbooking.exception;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Translates domain exceptions into HTTP responses so controllers stay thin.
 *
 * <p>Extends {@link ResponseEntityExceptionHandler} so Spring's own MVC exceptions (malformed
 * JSON, unknown route, wrong method, unconvertible path variable) keep their correct status
 * codes instead of being swallowed by the catch-all below.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoDriverAvailableException.class)
    public ResponseEntity<ApiError> handleNoDriverAvailable(NoDriverAvailableException ex) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler({RideNotFoundException.class, DriverNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RideAlreadyAssignedException.class)
    public ResponseEntity<ApiError> handleConflict(RideAlreadyAssignedException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidLocationException.class)
    public ResponseEntity<ApiError> handleInvalidLocation(InvalidLocationException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Catch-all for anything not handled above, so clients never see a raw stack trace. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    /** Collapses field-level validation failures into one readable message. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(status).body(apiError(status, message.isEmpty() ? "Invalid request" : message));
    }

    /** Re-shapes Spring's {@link ProblemDetail} responses into this API's uniform {@link ApiError}. */
    @Override
    protected ResponseEntity<Object> createResponseEntity(
            Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String message = body instanceof ProblemDetail detail && detail.getDetail() != null
                ? detail.getDetail()
                : reasonPhrase(status);
        return ResponseEntity.status(status).headers(headers).body(apiError(status, message));
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(apiError(status, message));
    }

    private ApiError apiError(HttpStatusCode status, String message) {
        return new ApiError(status.value(), reasonPhrase(status), message);
    }

    private String reasonPhrase(HttpStatusCode status) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        return resolved != null ? resolved.getReasonPhrase() : "Error";
    }
}
