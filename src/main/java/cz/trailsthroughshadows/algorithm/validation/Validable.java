package cz.trailsthroughshadows.algorithm.validation;

import java.util.List;

public interface Validable {
    public abstract List<String> validate();
    public abstract String toString();
}
