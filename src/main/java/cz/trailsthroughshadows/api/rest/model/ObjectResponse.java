package cz.trailsthroughshadows.api.rest.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ObjectResponse extends RestResponse {
    private final Object object;

    public ObjectResponse(HttpStatus status, Object object) {
        super(status);
        this.object = object;
    }

    public static ObjectResponse of(HttpStatus status, Object object) {
        return new ObjectResponse(status, object);
    }
}
