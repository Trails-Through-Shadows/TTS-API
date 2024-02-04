package cz.trailsthroughshadows.algorithm.validation;

import cz.trailsthroughshadows.ValidationConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        for (var e : errors) {
            log.debug(" > " + e);
        }

        return response;
    }

    @Autowired
    public void setValidationConfig(ValidationConfig validationConfig) {
        this.validationConfig = validationConfig;
    }
}
