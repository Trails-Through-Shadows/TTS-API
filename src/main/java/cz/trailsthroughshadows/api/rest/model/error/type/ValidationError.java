package cz.trailsthroughshadows.api.rest.model.error.type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationError extends MessageError {
    private String object;

    private String field;

    private Object rejectedValue;

    public ValidationError(String object, String field, Object rejectedValue, String message, Object... args) {
        super(message, args);
        this.object = object;
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    @Override
    public String toString() {
        return object + "." + field + ": " + getMessage() + " (rejected value: '" + rejectedValue + "')";
    }
}
