package cz.trailsthroughshadows.api.rest;

import lombok.Getter;

@Getter
public class RestResult extends Response {
    private final Object entry;

    public RestResult(Object entry) {
        this.status = Status.OK;

        if (entry instanceof String) {
            this.message = (String) entry;
            this.entry = null;
        } else {
            this.message = null;
            this.entry = entry;
        }
    }
}
