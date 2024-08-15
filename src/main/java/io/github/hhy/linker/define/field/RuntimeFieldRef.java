package io.github.hhy.linker.define.field;

/**
 * 运行时字段, 在运行时才能拿到真正的类型
 */
public class RuntimeFieldRef extends FieldRef {

    public RuntimeFieldRef(FieldRef prev, String name) {
        super(prev, name);
    }
}
