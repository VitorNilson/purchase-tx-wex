package wex.purchasetx.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConstraintViolationResponse  {
    private String message;
    private List<ValidationError> violations;
}
