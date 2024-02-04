package cz.trailsthroughshadows.algorithm.validation;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.error.RestSubError;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Slf4j
@Service
public class ValidationService {

    private ValidationConfig validationConfig;

    public String validate(Validable validable) {
        String name = validable.getValidableClass();
        String value = validable.getValidableValue();

        log.debug("Validating {} '{}'", name, value);

        Optional<RestError> error = validable.validate(validationConfig);
        boolean valid = error.isEmpty();

        String response = "%s '%s' is %s!".formatted(name, value, valid ? "valid" : "not valid");
        log.debug(response);

        if (!valid) {
            for (RestSubError subError : error.get().getErrors()) {
                log.debug("  - {}", subError.toString());
            }
            throw new RestException(error.get());
        }

        return response;
    }

    @Autowired
    public void setValidationConfig(ValidationConfig validationConfig) {
        this.validationConfig = validationConfig;
    }
}
