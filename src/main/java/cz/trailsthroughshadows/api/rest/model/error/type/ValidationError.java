package cz.trailsthroughshadows.api.rest.model.error.type;

import cz.trailsthroughshadows.api.rest.model.error.RestSubError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ValidationError extends RestSubError {
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    @Override
    public String toString() {
        return object + "." + field + ": " + message + " (rejected value: '" + rejectedValue + "')";
    }
}