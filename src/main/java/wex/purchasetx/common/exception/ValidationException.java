package wex.purchasetx.common.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {

    private List<ValidationError> violations;
    public ValidationException(String message, List<ValidationError> violations) {
        super(message);
        this.violations = violations;
    }
}
