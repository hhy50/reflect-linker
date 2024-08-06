package io.github.hhy.linker.define;


/**
 * 用来表示目标字段
 */

public class TargetField extends TargetPoint {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 上一个字段， 比如 a.b, 那么 this=b, prev=a;
     */
    private TargetField prev;

    public TargetField(TargetField prev, String name) {
        this.prev = prev;
        this.fieldName = name;
    }


    public TargetField getPrev() {
        return prev;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFullName() {
        String prefix = "";
        if (prev != null) {
            prefix = prev.getFullName();
        }
        return prefix + "_" + fieldName;
    }
}
