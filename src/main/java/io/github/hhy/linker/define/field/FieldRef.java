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
    public String objName;

    /**
     * 字段名
     */
    public String fieldName;

    /**
     * 上一级字段
     */
    private FieldRef prev;

    public FieldRef(FieldRef prev, String objName, String name) {
        this.prev = prev;
        this.objName = objName;
        this.fieldName = name;
    }

    public String getFullName() {
        String prefix = "";
        if (prev != null) {
            prefix = prev.getFullName()+"_$_";
        }
        return prefix+fieldName;
    }

    public String getGetterName() {
        return getFullName()+"_getter_mh";
    }

    public String getSetterName() {
        return getFullName()+"_setter_mh";
    }

    public Type getType() {
        return ObjectVar.TYPE;
    }

    public FieldRef getPrev() {
        return prev;
    }
}
