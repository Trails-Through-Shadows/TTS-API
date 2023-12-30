package cz.trailsthroughshadows.api.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
public class RestError {
    private Code code = Code.IM_A_TEAPOT;
    private String message = null;

    @AllArgsConstructor
    public enum Code {
        IM_A_TEAPOT(418),
        NOT_FOUND(404),
        INTERNAL_SERVER_ERROR(500);

        private final int statusCode;
    };
}
