package at.spengergasse.Mini.exceptions;

// Global Exception Handling
// ---------------------------------
// @RestControllerAdvice:
//   - Centralized exception handling for all @RestController classes
//   - Intercepts exceptions thrown from controller/service layers
//   - Converts exceptions to HTTP responses (ProblemDetail)
//   - Eliminates need for try-catch in controllers
//
// Exception Flow:
//   Controller/Service throws exception → @ExceptionHandler catches it ->
//   Logs error → Returns ProblemDetail (RFC 9457) with appropriate HTTP status
//
// RFC 9457 - Problem Details for HTTP APIs:
//   - Standard format for API error responses
//   - Machine-readable error information
//   - Consistent error structure across all endpoints
//   - Reference: https://www.rfc-editor.org/rfc/rfc9457.html
//
// Logging Strategy:
//   - log.warn()  -> Expected errors (validation, not found) - client errors (4xx)
//   - log.error() -> Unexpected errors (bugs, system failures) - server errors (5xx)
//
// Security:
//   - Never expose stack traces or internal details to clients
//   - Use generic messages for 500 errors
//   - Log full details server-side for debugging


// Exception Handling Strategy
// ---------------------------------
// 400 BAD_REQUEST       -> Validation failures (client error)
// 404 NOT_FOUND         -> Entity not found (client error)
// 500 INTERNAL_SERVER   -> Unexpected errors (server error)

import at.spengergasse.Mini.persistence.ShopRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 NOT_FOUND - Service Layer Exceptions
    // ---------------------------------
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ProblemDetail onServiceException(ServiceException ex)
    {
        // Log with context for monitoring
        log.warn("[404 NOT_FOUND] ServiceException: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }


    // 400 BAD_REQUEST - Domain / Guard Failures
    // ---------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ProblemDetail onValidation(IllegalArgumentException ex)
    {
        // Log validation failure with exception type
        log.warn("[400 BAD_REQUEST] Domain validation failed: {} ({})",
                ex.getMessage(), ex.getClass().getSimpleName());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        // e.g., "title must be 1..100 chars"
        problemDetail.setDetail(ex.getMessage());

        return problemDetail;
    }


    // 400 BAD_REQUEST - DTO Validation (@Valid)
    // ---------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ProblemDetail onDtoValidation(MethodArgumentNotValidException ex)
    {
        // Extract first validation error
        // e.g. "title: must not be blank"
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Validation failed");

        // Log with field count for context
        int errorCount = ex.getBindingResult().getFieldErrors().size();
        log.warn("[400 BAD_REQUEST] DTO validation failed: {} (total {} validation error(s))",
                msg, errorCount);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail(msg);
        return problemDetail;
    }


    // 400 BAD_REQUEST - JPA Bean Validation
    // ---------------------------------
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ProblemDetail onEntityValidation(jakarta.validation.ConstraintViolationException ex)
    {
        // Extract first constraint violation
        // e.g. "title: must not be blank"
        String msg = ex.getConstraintViolations().stream()
                .findFirst()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .orElse("Validation failed");

        // Log with violation count for context
        int violationCount = ex.getConstraintViolations().size();
        log.warn("[400 BAD_REQUEST] Entity validation failed: {} (total {} violation(s))",
                msg, violationCount);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setDetail(msg);
        return problemDetail;
    }


    // 500 INTERNAL_SERVER_ERROR - Catch-all for Unexpected Exceptions
    // ---------------------------------
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ProblemDetail onUnexpectedException(Exception ex)
    {
        // CRITICAL: Log full stack trace with exception type for debugging
        // This should trigger alerts in production monitoring
        log.error("[500 INTERNAL_SERVER_ERROR] Unexpected exception of type {}: {}",
                ex.getClass().getName(), ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        // SECURITY: Don't expose internal details to client in production
        // Only show generic message
        problemDetail.setDetail("An unexpected error occurred. Please contact support.");

        // Optional: In development, you might want to expose more details
        // problemDetail.setDetail(ex.getMessage());

        return problemDetail;
    }
}