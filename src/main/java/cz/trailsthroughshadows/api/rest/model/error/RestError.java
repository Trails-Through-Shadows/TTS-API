package cz.trailsthroughshadows.api.rest.model.error;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class RestError {
    private final HttpStatus status;
    private final String message;
    private final List<RestSubError> errors = new ArrayList<>();
    private LocalDateTime timestamp = LocalDateTime.now();

    public RestError(HttpStatus status, String message, Object... args) {
        this.status = status;
        this.message = message.formatted(args);
    }

    public static RestError of(HttpStatus code, String message, Object... args) {
        return new RestError(code, message, args);
    }

    public void addSubError(RestSubError error) {
        errors.add(error);
    }
}
