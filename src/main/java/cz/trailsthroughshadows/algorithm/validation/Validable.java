package cz.trailsthroughshadows.algorithm.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.error.RestSubError;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Validable {

    protected List<RestSubError> errors = new ArrayList<>();

    public Optional<RestError> validate(@Nullable ValidationConfig validationConfig) {
        errors = new ArrayList<>();
        validateInner(validationConfig);

        if (errors.isEmpty()) {
            return Optional.empty();
        }

        RestError error = new RestError(HttpStatus.NOT_ACCEPTABLE, "{} '{}' is not valid!", getValidableClass(), getValidableValue());

        for (var e : errors) {
            error.addSubError(e);
        }

        return Optional.of(error);
    }
    protected abstract void validateInner(@Nullable ValidationConfig validationConfig);

    @JsonIgnore
    public abstract String getValidableValue();

    @JsonIgnore
    public String getValidableClass() {
        String name = getClass().getSimpleName();
        name = name.endsWith("DTO") ? name.replace("DTO", "") : name;
        return name;
    }

}
