package cz.trailsthroughshadows.algorithm.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationResponse {

    private final boolean valid;
    private final String message;
    private final List<String> errors;
}
