package wex.purhcasetx.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wex.purhcasetx.exchangerate.exception.ExchangeRateNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ConstraintViolationResponse> handleValidationException(ValidationException exception) {

        return new ResponseEntity<>(ConstraintViolationResponse.builder()
                .violations(exception.getViolations())
                .message(exception.getMessage())
                .build(), null, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(UnsupportedDateFormatException.class)
    public ResponseEntity<ApiError> handleUnsupportedDateFormatException(UnsupportedDateFormatException exception) {
        return new ResponseEntity<>(ApiError.builder().message(exception.getMessage()).build(), null, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(ExchangeRateNotFoundException.class)
    public ResponseEntity<ApiError> handleExchangeRateNotFoundException(ExchangeRateNotFoundException exception) {
        return new ResponseEntity<>(ApiError.builder().message(exception.getMessage()).build(), null, HttpStatus.NOT_FOUND.value());
    }

}
