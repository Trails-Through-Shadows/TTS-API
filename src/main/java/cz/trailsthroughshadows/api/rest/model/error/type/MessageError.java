package cz.trailsthroughshadows.api.rest.model.error.type;

import cz.trailsthroughshadows.api.rest.model.error.RestSubError;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MessageError extends RestSubError {
    private String message;

    public MessageError(String message, Object... args) {
        this.message = message.formatted(args);
    }

    @Override
    public String toString() {
        return message;
    }
}