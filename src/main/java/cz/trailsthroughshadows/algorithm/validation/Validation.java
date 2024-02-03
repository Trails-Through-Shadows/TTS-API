package cz.trailsthroughshadows.algorithm.validation;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Validation {
    public static ValidationResponse validate(Validable validable) {
        String name = validable.getClass().getSimpleName();
        String str = validable.toString();

        log.info("Validating {} '{}'", name, str);
        List<String> errors = validable.validate();
        boolean valid = errors.isEmpty();

        ValidationResponse response = new ValidationResponse(
                valid,
                "%s '%s' is %s!".formatted(name, str, valid ? "valid" : "not valid"),
                errors);

        log.info(response.getMessage());

        return response;
    }
}
