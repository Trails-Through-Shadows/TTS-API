package cz.trailsthroughshadows.api.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RestResult {
    private Pagination pagination;
    private List<?> entries;
}
