package cz.trailsthroughshadows.api.rest.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
