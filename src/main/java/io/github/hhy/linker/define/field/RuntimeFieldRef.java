package io.github.hhy.linker.define.field;

public class RuntimeFieldRef extends FieldRef{

    public RuntimeFieldRef(FieldRef prev, String objName, String fieldName) {
        super(prev, objName, fieldName);
    }
}
