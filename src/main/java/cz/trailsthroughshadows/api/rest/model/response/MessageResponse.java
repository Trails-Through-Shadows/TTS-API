package cz.trailsthroughshadows.api.rest.model.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MessageResponse extends RestResponse {
    private final String message;

    public MessageResponse(HttpStatus status, String message, Object... args) {
        super(status);
        this.message = message.formatted(args);
    }

    public static MessageResponse of(HttpStatus status, String message, Object... args) {
        return new MessageResponse(status, message, args);
    }
}
