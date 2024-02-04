package cz.trailsthroughshadows.algorithm.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Validable {

    protected List<String> errors = new ArrayList<>();

    public List<String> validate(@Nullable ValidationConfig validationConfig) {
        errors = new ArrayList<>();
        validateInner(validationConfig);
        return errors;
    }
    protected abstract void validateInner(@Nullable ValidationConfig validationConfig);

    @JsonIgnore
    public abstract String getIdentifier();

}
