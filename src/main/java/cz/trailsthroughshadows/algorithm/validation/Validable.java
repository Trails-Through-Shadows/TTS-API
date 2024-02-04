package cz.trailsthroughshadows.algorithm.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.trailsthroughshadows.ValidationConfig;
import jakarta.annotation.Nullable;

import java.util.List;

public interface Validable {

    List<String> validate(@Nullable ValidationConfig validationConfig);

    @JsonIgnore
    String getIdentifier();

}
