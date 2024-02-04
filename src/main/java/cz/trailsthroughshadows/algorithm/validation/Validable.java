package cz.trailsthroughshadows.algorithm.validation;

import java.util.List;

public interface Validable {
    List<String> validate();
    String getIdentifier();
}
