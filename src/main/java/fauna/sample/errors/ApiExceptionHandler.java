package fauna.sample.errors;

import com.fauna.exception.AbortException;
import com.fauna.exception.ConstraintFailureException;
import com.fauna.exception.NullDocumentException;
import com.fauna.exception.QueryRuntimeException;
import com.fauna.exception.ServiceException;
import com.fauna.response.ConstraintFailure;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AbortException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            AbortException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(ConstraintFailureException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            ConstraintFailureException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(QueryRuntimeException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            QueryRuntimeException ex) {
        if (Objects.equals(ex.getErrorCode(), "document_not_found")) {
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
            apiError.setMessage(ex.getMessage());
            return new ResponseEntity<>(apiError, apiError.getStatus());
        }

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
