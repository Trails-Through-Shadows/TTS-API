package cz.trailsthroughshadows.algorithm.validation.text;

import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;

public class Title extends Text {

    public Title(String text) {
        super(text);
    }

    @Override
    protected void validateInner(ValidationConfig validationConfig) {
        validateText(validationConfig.getTitle().getMinLen(), validationConfig.getTitle().getMaxLen(), validationConfig.getTitle().getAllowedChars());
    }
}
