package cz.trailsthroughshadows.algorithm.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.error.RestSubError;
import cz.trailsthroughshadows.api.rest.model.error.type.CompositeError;
import jakarta.annotation.Nullable;
import jakarta.persistence.Transient;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public abstract class Validable {

    /**
     * List of errors that occurred during validation.
     */
    @Transient
    protected List<RestSubError> errors = new ArrayList<>();

    /**
     * Custom field name for when the name has to be different then the class name.
     */
    @Transient
    protected String fieldName;

    /**
     * Validates the object using the provided validation configuration.
     *
     * @param validationConfig The validation configuration to use.
     * @return An optional containing a RestError if the object is not valid, empty
     *         otherwise.
     */
    public Optional<RestError> validate(@Nullable ValidationConfig validationConfig) {
        return validate(validationConfig, null);
    }

    /**
     * Validates the object using the provided validation configuration.
     *
     * @param validationConfig The validation configuration to use.
     * @param fieldName        Custom field name for when the name has to be different then the class name.
     * @return An optional containing a RestError if the object is not valid, empty
     *         otherwise.
     */
    public Optional<RestError> validate(@Nullable ValidationConfig validationConfig, String fieldName) {

        if (fieldName != null) {
            this.fieldName = fieldName;
        }

        String name = getValidableClass() + getValidableValueFormatted();
        log.trace("Validating {}", name);

        errors = new ArrayList<>();
        validateInner(validationConfig);

        if (errors.isEmpty()) {
            return Optional.empty();
        }

        RestError error = new RestError(HttpStatus.NOT_ACCEPTABLE, "{} is not valid!", name);

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
        validateChild(child, validationConfig, null);
    }

    /**
     * Validates a child object using the provided validation configuration.
     *
     * @param child            The child object to validate.
     * @param validationConfig The validation configuration to use.
     * @param fieldName        Custom field name for when the name has to be different then the class name.
     */
    protected void validateChild(Validable child, @Nullable ValidationConfig validationConfig, String fieldName) {
        if (child == null)
            return;

        Optional<RestError> error = child.validate(validationConfig, fieldName);

        if (error.isPresent()) {
            RestError restError = error.get();
            RestSubError composite = new CompositeError(child.getValidableClass(), restError.getErrors(),
                    restError.getMessage());
            errors.add(composite);
        }
    }

    /**
     * Returns a string representation of the validable object and its value.
     *
     * @return String representation of the object.
     */
    @Transient
    @JsonIgnore
    public abstract String getValidableValue();

    /**
     * Returns the name of the validable object's class.
     *
     * @return The name of the object's class.
     */
    @Transient
    @JsonIgnore
    public String getValidableClass() {
        String name = getClass().getSimpleName();
        name = name.endsWith("DTO") ? name.replace("DTO", "") : name;
        if (fieldName == null) {
            return name;
        }
        return fieldName;
    }

    /**
     * Returns the value of the validable object formatted for use in a message.
     *
     * @return The formatted value of the object.
     */
    @Transient
    @JsonIgnore
    public String getValidableValueFormatted() {
        return getValidableValue() == null ? "" : " '%s'".formatted(getValidableValue());
    }
}
