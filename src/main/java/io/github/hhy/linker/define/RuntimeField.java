package io.github.hhy.linker.define;


import io.github.hhy.linker.bytecode.getter.Getter;

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
        return getFullName()+"_getter_mh";
    }

    public String getSetterMhVarName() {
        return getFullName()+"_setter_mh";
    }

    public String getNullErrorVar() {
        if (prev == null || prev == TARGET) {
            return "null."+fieldName;
        }
        return prev.fieldName+"[null]."+fieldName;
    }
}
