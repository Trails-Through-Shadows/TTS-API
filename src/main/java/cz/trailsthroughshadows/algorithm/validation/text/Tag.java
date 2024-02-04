package cz.trailsthroughshadows.algorithm.validation.text;

import cz.trailsthroughshadows.algorithm.validation.ValidationConfig;

public class Tag extends Text {

    public Tag(String text) {
        super(text);
    }

    @Override
    protected void validateInner(ValidationConfig validationConfig) {
        validateText(validationConfig.getTag().getMinLen(), validationConfig.getTag().getMaxLen(), validationConfig.getTag().getAllowedChars());

        // check prefix regex
        if (validationConfig.getTag().getPrefix() != null && !text.matches(validationConfig.getTag().getPrefix())) {
            errors.add("Tag has to start with a correct prefix!");
        }
    }
}
