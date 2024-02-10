package cz.trailsthroughshadows.algorithm.validation.text;

import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;
import jakarta.annotation.Nullable;

public class Description extends Text {
    public Description(String text) {
        super(text);
    }

    @Override
    protected void validateInner(@Nullable ValidationConfig validationConfig) {
        validateText(0, validationConfig.getDescription().getMaxLen(), validationConfig.getDescription().getAllowedChars());
    }
}
