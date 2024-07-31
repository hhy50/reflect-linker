package io.github.hhy.linker.define;


import lombok.Getter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Target$Method extends Target {

    @Getter
    private Method method;

    /**
     * 持有方法的类
     */
    @Getter
    private Class<?> owner;

    /**
     * 方法名
     */
    @Getter
    private String methodName;

    /**
     * 是否是静态字段
     */
    @Getter
    private boolean isStatic;

    /**
     * 是否是私有字段
     */
    @Getter
    private boolean isPrivate;

    @Getter
    private boolean isSuperCall;

    public Target$Method(Method method) {
        this.method = method;
        this.owner = method.getDeclaringClass();
        this.methodName = method.getName();
        this.isStatic = (method.getModifiers() & Modifier.STATIC) > 0;
        this.isPrivate = (method.getModifiers() & Modifier.PRIVATE) > 0;
    }

    public static Target create(Method rMethod) {
        return new Target$Method(rMethod);
    }
}
