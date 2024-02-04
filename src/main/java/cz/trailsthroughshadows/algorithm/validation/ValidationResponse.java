package cz.trailsthroughshadows.algorithm.validation;

import java.util.List;

public record ValidationResponse(boolean valid, String message, List<String> errors) {

}
