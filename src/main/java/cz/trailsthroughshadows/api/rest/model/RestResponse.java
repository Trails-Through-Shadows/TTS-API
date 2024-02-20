package cz.trailsthroughshadows.api.rest.model;

import org.springframework.http.HttpStatus;

public class RestResponse {
    private final HttpStatus status;

    public RestResponse(HttpStatus status) {
        this.status = status;
    }

    public RestResponse of(HttpStatus status) {
        return new RestResponse(status);
    }
}
