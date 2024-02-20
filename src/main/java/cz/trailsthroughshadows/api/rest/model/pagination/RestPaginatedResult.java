package cz.trailsthroughshadows.api.rest.model.pagination;

import lombok.Data;

import java.util.Collection;

@Data
public class RestPaginatedResult<T> {

    private final Pagination pagination;

    private final Collection<T> entries;

    public static <T> RestPaginatedResult<T> of(Pagination pagination, Collection<T> entries) {
        return new RestPaginatedResult<>(pagination, entries);
    }
}
