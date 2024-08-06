package io.github.hhy.linker.define;


/**
 * 用来表示目标字段
 */

public abstract class Field extends TargetPoint {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 上一个字段， 比如 a.b, 那么 this=b, prev=a;
     */
    private Field prev;

    public Field(Field prev, String name) {
        this.prev = prev;
        this.fieldName = name;
    }


    public Field getPrev() {
        return prev;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFullName() {
        String prefix = "";
        if (prev != null) {
            prefix = prev.getFullName() + "_$_";
        }
        return prefix + fieldName;
    }

    public String getGetterMhVarName() {
        return getFullName() + "_mh_getter";
    }
}
