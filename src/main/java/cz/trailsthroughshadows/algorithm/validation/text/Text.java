package cz.trailsthroughshadows.algorithm.validation.text;

import cz.trailsthroughshadows.algorithm.validation.Validable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Text extends Validable {

    protected String text;

    public void validateText(int minLen, int maxLen, String allowedChars) {
        String name = this.getClass().getSimpleName();

        // check min and max length
        if (text == null || text.isBlank()) {
            errors.add("%s is required!".formatted(name));
        } else {
            if (minLen != 0 && text.length() < minLen) {
                errors.add("%s has to be at least %d characters long!".formatted(name, minLen));
            }
            if (maxLen != 0 && text.length() > maxLen) {
                errors.add("%s has to be at most %d characters long!".formatted(name, maxLen));
            }
        }

        // check allowed regex
        if (allowedChars != null && !text.matches(allowedChars)) {
            errors.add("%s contains disallowed characters!".formatted(name));
        }
    }

    @Override
    public String getIdentifier() {
        return text;
    }
}
