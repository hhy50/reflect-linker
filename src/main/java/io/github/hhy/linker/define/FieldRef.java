package io.github.hhy.linker.define;


import io.github.hhy.linker.bytecode.getter.Getter;

/**
 * 用来表示目标字段
 */
public class FieldRef extends TargetPoint {

    public static final FieldRef TARGET = new FieldRef(null, "target");

    /**
     * 字段名
     */
    public String fieldName;

    /**
     * 对应的getter
     */
    public Getter getter;

    /**
     * 上一个字段， 比如 a.b, 那么 this=b, prev=a;
     */
    private FieldRef prev;

    public FieldRef(FieldRef prev, String name) {
        this.prev = prev;
        this.fieldName = name;
    }

    public FieldRef getPrev() {
        return prev;
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

    public String getLookupName() {
        return getFullName()+"_lookup";
    }

    public String getFieldRef(String fieldName) {
        if (prev == null) {
            return fieldName;
        }
        return prev.getFieldRef(this.fieldName)+"."+fieldName;
    }
}
