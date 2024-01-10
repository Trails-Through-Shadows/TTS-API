package cz.trailsthroughshadows.api.rest;

import lombok.Getter;

@Getter
public class RestError extends Response {
    private final int code;

    public RestError(int code, Status status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
