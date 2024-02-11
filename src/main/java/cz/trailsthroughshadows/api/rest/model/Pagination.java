package cz.trailsthroughshadows.api.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pagination {

    private int count;

    private boolean hasMoreEntries;

    private int totalEntries;

    private int page;

    private int limit;

}
