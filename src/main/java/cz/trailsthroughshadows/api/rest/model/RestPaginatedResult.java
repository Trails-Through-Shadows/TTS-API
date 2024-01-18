package cz.trailsthroughshadows.api.rest.model;

import lombok.Data;

import java.util.List;

@Data
public class RestPaginatedResult<T> {

    private final Pagination pagination;
    private final List<T> entries;

    public static <T> RestPaginatedResult<T> of(Pagination pagination, List<T> entries) {
        return new RestPaginatedResult<>(pagination, entries);
    }
}
