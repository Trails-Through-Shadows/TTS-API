package cz.trailsthroughshadows.api.rest.model.error.type;

import cz.trailsthroughshadows.api.rest.model.error.RestSubError;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompositeError extends MessageError {

    private String object;

    private List<RestSubError> errors;

    public CompositeError(String object, List<RestSubError> errors, String message, Object... args) {
        super(message, args);
        this.object = object;
        this.errors = errors;
    }

    @Override
    public String toString() {
        return message + " (" + errors.size() + " error" + (errors.size() <= 1 ? "" : "s") + ")";
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent(indent)).append(message).append(" (").append(errors.size()).append(" error").append(errors.size() <= 1 ? "" : "s").append(")");

        for (var e : errors) {
            sb.append("\n").append(e.toString(indent + 1));
        }

        return sb.toString();
    }

}
