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

    /**
     * List of errors that occurred during validation.
     */
    protected List<RestSubError> errors = new ArrayList<>();

    /**
     * Validates the object using the provided validation configuration.
     *
     * @param validationConfig The validation configuration to use.
     * @return An optional containing a RestError if the object is not valid, empty otherwise.
     */
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

    /**
     * Inner validation method that should be implemented by the extending class.
     * This method should validate the object and add any errors to the errors list.
     *
     * @param validationConfig The validation configuration to use.
     */
    protected abstract void validateInner(@Nullable ValidationConfig validationConfig);

    /**
     * Validates a child object using the provided validation configuration.
     *
     * @param child            The child object to validate.
     * @param validationConfig The validation configuration to use.
     */
    protected void validateChild(Validable child, @Nullable ValidationConfig validationConfig) {
        Optional<RestError> error = child.validate(validationConfig);
        error.ifPresent(restError -> errors.addAll(restError.getErrors()));
    }

    /**
     * Returns a string representation of the validable object and its value.
     *
     * @return String representation of the object.
     */
    @JsonIgnore
    public abstract String getValidableValue();

    /**
     * Returns the name of the validable object's class.
     *
     * @return The name of the object's class.
     */
    @JsonIgnore
    public String getValidableClass() {
        String name = getClass().getSimpleName();
        name = name.endsWith("DTO") ? name.replace("DTO", "") : name;
        return name;
    }

}
