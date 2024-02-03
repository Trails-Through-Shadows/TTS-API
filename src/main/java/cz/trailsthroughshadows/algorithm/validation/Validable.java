package cz.trailsthroughshadows.algorithm.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public interface Validable {

    List<String> validate();

    @JsonIgnore
    String getIdentifier();

}
