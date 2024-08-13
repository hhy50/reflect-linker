package io.github.hhy.linker.define.field;

import io.github.hhy.linker.define.FieldRef;

import java.lang.reflect.Field;

/**
 * 易损字段, 顾名思义在运行过程中字段的类型随时可能会被子类替代
 * 这个类型只能从前面的EarlyFieldRef的传递下来
 */
public class VulnerableFieldRef extends FieldRef {

    public VulnerableFieldRef(FieldRef prev, Field field) {
        super(prev, field.getName());
    }
}
