package cz.trailsthroughshadows.api.rest.model;

import lombok.Data;

@Data
public class RestResponse {
    private final String message;

    public RestResponse(String message, Object... args) {
        this.message = message.formatted(args);
    }

    public static RestResponse of(String message, Object... args) {
        return new RestResponse(message, args);
    }
}
