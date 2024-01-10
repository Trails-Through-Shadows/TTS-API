package cz.trailsthroughshadows.api.rest;

import lombok.Getter;

import java.util.List;

@Getter
public class RestPaginatedResult extends Response {
    private final Pagination pagination;
    private final List<?> entries;

    public RestPaginatedResult(Pagination pagination, List<?> data) {
        this.status = Status.OK;
        this.message = null;
        this.pagination = pagination;
        this.entries = data;
    }
}
