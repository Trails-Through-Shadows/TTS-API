package cz.trailsthroughshadows.algorithm.validation;

import cz.trailsthroughshadows.algorithm.util.Sanitized;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Slf4j
@Service
public class ValidationService {

    private ValidationConfig validationConfig;

    /**
     * Validates an object that implements the Validable interface.
     *
     * @param validable Optional containing the object to validate.
     *
     * @return A string with the result of the validation.
     * @throws RestException Structured response with the error, if the object is not valid.
     */
    public String validate(Optional<? extends Validable> validable) throws RestException {
        if (validable.isEmpty()) {
            String response = "Validable object is null!";
            log.debug(response);
            throw new RestException(new RestError(HttpStatus.NOT_ACCEPTABLE, response));
        }

        Validable v = validable.get();

        String name = v.getValidableClass();
        String value = v.getValidableValue();

        log.debug("Validating {} '{}'", name, value);

        Optional<RestError> error = v.validate(validationConfig);
        boolean valid = error.isEmpty();

        String response = Sanitized.format("{} '{}' is {}valid!", name, value, valid ? "" : "in");

        if (!valid) {
            String errorStr = error.get().toString();
            for (String line : errorStr.split("\n")) {
                log.debug(line);
            }

            throw new RestException(error.get());
        }

        log.debug(response);
        return response;
    }

    /**
     * Validates an object that implements the Validable interface.
     *
     * @param validable The object to validate.
     *
     * @return A string with the result of the validation.
     * @throws RestException Structured response with the error, if the object is not valid.
     */
    public String validate(Validable validable) throws RestException {
        return validate(Optional.of(validable));
    }

    @Autowired
    public void setValidationConfig(ValidationConfig validationConfig) {
        this.validationConfig = validationConfig;
    }
}
