package cz.trailsthroughshadows.api.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Pagination {
    private int count;
    private boolean hasMoreEntries;
    private int totalItems;
    private int skipCount;
    private int maxEntries;
}
