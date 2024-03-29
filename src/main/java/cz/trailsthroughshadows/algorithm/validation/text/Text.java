package cz.trailsthroughshadows.algorithm.validation.text;

import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.api.rest.model.error.type.ValidationError;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Text extends Validable {

    protected String text;

    public void validateText(int minLen, int maxLen, String allowedChars) {

        String className = getValidableClass();

        if (minLen == 0 && (text == null || text.isBlank())) {
            return;
        }

        // check min and max length
        if (minLen != 0 && (text == null || text.isBlank())) {
            errors.add(new ValidationError(className, "text", text, "%s is required!".formatted(className)));
            return;
        }

        if (minLen != 0 && text.length() < minLen) {
            errors.add(new ValidationError(className, "text", text,
                    "%s has to be at least %d characters long!".formatted(className, minLen)));
        }
        if (maxLen != 0 && text.length() > maxLen) {
            errors.add(new ValidationError(className, "text", text,
                    "%s has to be at most %d characters long!".formatted(className, maxLen)));
        }

        // check allowed regex
        if (allowedChars != null && !text.matches(allowedChars)) {
            errors.add(new ValidationError(className, "text", text,
                    "%s contains disallowed characters!".formatted(className)));
        }
    }

    @Override
    public String getValidableValue() {
        return text;
    }
}
