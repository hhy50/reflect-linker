package io.github.hhy.linker.bytecode.vars;

import org.objectweb.asm.Type;

public class FieldVar extends ObjectVar {

    public final String fieldName;

    public FieldVar(int lvbIndex, Type type, String fieldName) {
        super(lvbIndex, type);
        this.fieldName = fieldName;
    }

    public String getFullName() {
        return fieldName+"[type="+type.getClassName()+"]";
    }
}
