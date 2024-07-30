package io.github.hhy.linker.define;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Data
@EqualsAndHashCode(callSuper=false)
public class MethodTarget extends Target {
    /**
     * 持有方法的类
     */
    private Class<?> owner;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 是否是静态字段
     */
    private boolean isStatic;

    /**
     * 是否是私有字段
     */
    private boolean isPrivate;

    public MethodTarget(Method method) {
        this.owner = method.getDeclaringClass();
        this.methodName = method.getName();
        this.isStatic = (method.getModifiers() & Modifier.STATIC) > 0;
        this.isPrivate = (method.getModifiers() & Modifier.PRIVATE) > 0;
    }
}
