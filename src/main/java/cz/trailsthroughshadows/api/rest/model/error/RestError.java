package cz.trailsthroughshadows.api.rest.model.error;

import cz.trailsthroughshadows.algorithm.util.Sanitized;
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
        this.message = Sanitized.format(message, args);
    }

    public RestError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static RestError of(HttpStatus code, String message, Object... args) {
        return new RestError(code, message, args);
    }

    public void addSubError(RestSubError error) {
        errors.add(error);
    }

    public void union(RestError error) {
        errors.addAll(error.errors);
    }

    public void addTo(List<RestSubError> errors) {
        errors.addAll(this.errors);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage());

        for (var e : errors) {
            sb.append("\n").append(e.toString(1));
        }

        return sb.toString();
    }
}
