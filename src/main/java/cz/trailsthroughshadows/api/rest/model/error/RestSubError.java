package cz.trailsthroughshadows.api.rest.model.error;

public abstract class RestSubError {

    public String toString(int indent) {
        return getIndent(indent) + this;
    }

    public String getIndent(int indent) {
        return "  ".repeat(indent);
    }
}
