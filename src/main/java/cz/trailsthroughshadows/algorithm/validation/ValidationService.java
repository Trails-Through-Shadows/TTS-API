package cz.trailsthroughshadows.algorithm.validation;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.error.type.MessageError;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Slf4j
@Service
public class ValidationService {

    private ValidationConfig validationConfig;

    public ValidationResponse validate(Validable validable) {
        String name = validable.getClass().getSimpleName();
        name = name.endsWith("DTO") ? name.replace("DTO", "") : name;
        String str = validable.getIdentifier();

        log.debug("Validating {} '{}'", name, str);

        List<String> errors = validable.validate(validationConfig);
        boolean valid = errors.isEmpty();

        ValidationResponse response = new ValidationResponse(
            valid,
            "%s '%s' is %s!".formatted(name, str, valid ? "valid" : "not valid"),
            errors);

        log.debug(response.message());

        if (!valid) {
            RestError error = new RestError(HttpStatus.NOT_ACCEPTABLE, response.message());
            for (var e : errors) {
                log.debug(" > " + e);
                error.addSubError(new MessageError(e));
            }

            throw new RestException(error);
        }

        return response;
    }

    @Autowired
    public void setValidationConfig(ValidationConfig validationConfig) {
        this.validationConfig = validationConfig;
    }
}
