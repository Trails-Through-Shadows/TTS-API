package cz.trailsthroughshadows.api.rest.exception;

import cz.trailsthroughshadows.api.rest.model.error.RestError;
import jakarta.persistence.PersistenceException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class RestException extends PersistenceException {

    private final RestError error;

    public static RestException of(HttpStatus code, String message, Object... args) {
        return new RestException(RestError.of(code, message, args));
    }
}
