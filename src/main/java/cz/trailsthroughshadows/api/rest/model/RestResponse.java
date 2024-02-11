package cz.trailsthroughshadows.api.rest.model;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class RestResponse {
    private final HttpStatus status;

    private final String message;

    public RestResponse(HttpStatus status, String message, Object... args) {
        this.status = status;
        this.message = message.formatted(args);
    }

    public static ResponseEntity<RestResponse> of(HttpStatus status, String message, Object... args) {
        return new ResponseEntity<RestResponse>(new RestResponse(status, message, args), status);
    }
}
