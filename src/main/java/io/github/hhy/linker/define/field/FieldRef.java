package io.github.hhy.linker.define.field;


import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;

/**
 * 用来表示目标字段
 */
public abstract class FieldRef {

    /**
     * 对象名
     */
    private String objName;

    /**
     * 字段名
     */
    public String fieldName;

    private String fullName;

    /**
     * 上一级字段
     */
    private FieldRef prev;

    public FieldRef(FieldRef prev, String objName, String name) {
        this.prev = prev;
        this.objName = objName;
        this.fieldName = name;
    }

    public String getUniqueName() {
        String prefix = "";
        if (prev != null) {
            prefix = prev.getUniqueName()+"_$_";
        }
        return prefix+fieldName;
    }

    public String getGetterName() {
        return getUniqueName()+"_getter_mh";
    }

    public String getSetterName() {
        return getUniqueName()+"_setter_mh";
    }

    public Type getType() {
        return ObjectVar.TYPE;
    }

    public FieldRef getPrev() {
        return prev;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        if (fullName == null) {
            return getUniqueName();
        }
        return fullName;
    }
}
