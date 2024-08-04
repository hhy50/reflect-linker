package io.github.hhy.linker.define;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TargetMethod extends TargetPoint {
    private Method method;

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

    private boolean isSuperCall;

    public TargetMethod(Method method) {
        this.method = method;
        this.owner = method.getDeclaringClass();
        this.methodName = method.getName();
        this.isStatic = (method.getModifiers() & Modifier.STATIC) > 0;
        this.isPrivate = (method.getModifiers() & Modifier.PRIVATE) > 0;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getOwner() {
        return owner;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isSuperCall() {
        return isSuperCall;
    }
}
