package io.github.hhy.linker.define;


import io.github.hhy.linker.bytecode.Getter;

/**
 * 用来表示目标字段
 */
public class RuntimeField extends TargetPoint {

    public static final RuntimeField TARGET = new RuntimeField(null, "target");

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
    private RuntimeField prev;

    public RuntimeField(RuntimeField prev, String name) {
        this.prev = prev;
        this.fieldName = name;
    }

    public RuntimeField getPrev() {
        return prev;
    }

    public String getFullName() {
        String prefix = "";
        if (prev != null) {
            prefix = prev.getFullName()+"_$_";
        }
        return prefix+fieldName;
    }

    public String getGetterMhVarName() {
        return getFullName()+"_mh_getter";
    }

    public String getNullErrorVar() {
        String prefix = "";
        if (prev != null) {
            prefix = prev.fieldName+".";
        }
        return prefix+"[null]."+fieldName;
    }
}
