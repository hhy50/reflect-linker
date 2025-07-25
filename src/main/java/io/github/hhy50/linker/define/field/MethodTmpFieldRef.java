package io.github.hhy50.linker.define.field;

import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.util.ReflectUtil;

import java.lang.reflect.Method;

/**
 * 方法执行后的临时变量引用
 */
public class MethodTmpFieldRef extends FieldRef {

    private final MethodRef methodRef;

    /**
     * Instantiates a new Field ref.
     *
     * @param name the name
     */
    public MethodTmpFieldRef(MethodRef methodRef, String name) {
        super(null, name);
        this.methodRef = methodRef;
    }

    @Override
    public Method findMethod(String methodName, String[] argsType, String superClass) {
        if (methodRef instanceof EarlyMethodRef) {
            Class<?> returnType = ((EarlyMethodRef) methodRef).getReturnType();
            return ReflectUtil.matchMethod(returnType, methodName, superClass, argsType);
        }
        return null;
    }

    public MethodRef getMethodRef() {
        return methodRef;
    }
}
