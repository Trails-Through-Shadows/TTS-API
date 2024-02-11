package cz.trailsthroughshadows.api.rest.model.error.type;

import cz.trailsthroughshadows.algorithm.util.Sanitized;
import cz.trailsthroughshadows.api.rest.model.error.RestSubError;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageError extends RestSubError {
    protected String message;

    public MessageError(String message, Object... args) {
        this.message = Sanitized.format(message, args);
    }

    @Override
    public String toString() {
        return message;
    }
}