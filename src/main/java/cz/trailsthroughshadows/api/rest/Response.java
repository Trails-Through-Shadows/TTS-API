package cz.trailsthroughshadows.api.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Data
public abstract class Response {
    protected Status status;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected String message;

    @AllArgsConstructor
    public enum Status {
        OK(200),
        CREATED(201),
        ACCEPTED(202),
        NO_CONTENT(204),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        NOT_FOUND(404),
        METHOD_NOT_ALLOWED(405),
        CONFLICT(409),
        IM_A_TEAPOT(418),
        INTERNAL_SERVER_ERROR(500),
        SERVICE_UNAVAILABLE(503);

        private final int statusCode;

        public ResponseEntity<?> getErrorCode(String message) {
            return ResponseEntity.status(statusCode).body(new RestError(statusCode, this, message));
        }

        public ResponseEntity<?> getResult(Object entry) {
            return ResponseEntity.status(statusCode).body(new RestResult(entry));
        }

        public ResponseEntity<?> getResult(Pagination pagination, List<?> entries) {
            return ResponseEntity.status(statusCode).body(new RestPaginatedResult(pagination, entries));
        }

        @Override
        public String toString() {
            return name();
        }
    };
}
