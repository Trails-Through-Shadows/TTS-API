package cz.trailsthroughshadows.api.rest.model.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class IdResponse extends RestResponse {
    private final Integer id;

    public IdResponse(HttpStatus status, Integer id) {
        super(status);
        this.id = id;
    }

    public static IdResponse of(HttpStatus status, Integer id) {
        return new IdResponse(status, id);
    }
}
