package io.github.hhy.linker.define;

public class TargetField extends Field {

    private final String targetClass;

    public TargetField(String targetClass) {
        super(null, "target");
        this.targetClass = targetClass;
    }
}
