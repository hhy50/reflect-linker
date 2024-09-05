package io.github.hhy.linker.define.field;

public class RuntimeFieldRef extends FieldRef {

    /**
     * 是否指定了静态
     */
    private boolean designateStatic;

    /**
     * 是否是静态
     */
    private boolean isStatic;

    public RuntimeFieldRef(FieldRef prev, String objName, String fieldName) {
        super(prev, objName, fieldName);
    }

    public boolean isDesignateStatic() {
        return designateStatic;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void designateStatic(boolean isStatic) {
        this.designateStatic = true;
        this.isStatic = isStatic;
    }
}
