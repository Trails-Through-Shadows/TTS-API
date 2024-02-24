package cz.trailsthroughshadows.api.rest.model.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestResponse {
    private final HttpStatus status;

    public RestResponse(HttpStatus status) {
        this.status = status;
    }

    public RestResponse of(HttpStatus status) {
        return new RestResponse(status);
    }
}
