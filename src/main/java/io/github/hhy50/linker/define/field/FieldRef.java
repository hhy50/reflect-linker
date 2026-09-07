package io.github.hhy50.linker.define.field;


import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;

import java.util.List;

/**
 * The type Field ref.
 */
public abstract class FieldRef  {

    /**
     * The Field name.
     */
    protected String name;

    private boolean nullable;

    private Object defaultValue;

    private List<Object> indexs;

    /**
     * Instantiates a new Field ref.
     *
     * @param name     the name
     */
    public FieldRef(String name) {
        this.name = name;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * 获取字段在生成的代码中的占位类型
     * 1. 如果字段实际类型是不可访问（default, private） 会使用object代替
     * @return the type
     */
    public Type getType() {
        return ObjectVar.TYPE;
    }

    /**
     * 获取字段的实际类型, 这个函数有两个作用
     * 1. 辅助《如果字段表达式后面跟着[]数组访问符，那么这个函数返回的类型和表达式返回的类型就不一样》推到当前表达式的返回值类型
     * 2. 使用真实的class类型获取具体的 MethodHandle
     * @return the actual type
     */
    public Class<?> getFieldActualType() {
        return Object.class;
    }

    /**
     * Sets static.
     *
     * @param isStatic the is static
     */
    public void setStatic(boolean isStatic) {

    }

    /**
     * Sets nullable.
     *
     * @param nullable the nullable
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * Is nullable boolean.
     *
     * @return the boolean
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Sets index.
     *
     * @param indexs the indexs
     */
    public void setIndex(List<Object> indexs) {
        this.indexs = indexs;
    }

    /**
     * Gets indexs.
     *
     * @return the indexs
     */
    public List<Object> getIndexs() {
        return indexs;
    }
}