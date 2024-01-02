package cz.trailsthroughshadows.api.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestError {
    private int code;
    private String status;
    private String message;

    @AllArgsConstructor
    public enum Type {
        IM_A_TEAPOT(418),
        NOT_FOUND(404),
        INTERNAL_SERVER_ERROR(500);

        private final int statusCode;

        public RestError getErrorCode(String message) {
            return new RestError(statusCode, name(), message);
        }
    };
}
