package io.github.hhy50.linker.define.field;

import io.github.hhy50.linker.define.method.MethodRef;

/**
 * 方法执行后的临时变量引用
 */
public class MethodTmpFieldRef extends FieldRef {

    private final MethodRef methodRef;

    private Class<?> actualType;

    /**
     * Instantiates a new Field ref.
     *
     * @param name the name
     */
    public MethodTmpFieldRef(MethodRef methodRef, String name) {
        super(null, name);
        this.methodRef = methodRef;
    }

    public MethodRef getMethodRef() {
        return methodRef;
    }

    @Override
    public Class<?> getActualType() {
        if (this.actualType != null) {
            return this.actualType;
        }
        return super.getActualType();
    }

    public void setActualType(Class<?> clazz) {
        this.actualType = clazz;
    }
}
