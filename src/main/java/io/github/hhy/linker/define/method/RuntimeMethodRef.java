package io.github.hhy.linker.define.method;


import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.field.FieldRef;
import org.objectweb.asm.Type;

import java.util.Arrays;


public class RuntimeMethodRef extends MethodRef {
    private Type[] argsType;
    private Class<?> returnType;

    public RuntimeMethodRef(FieldRef owner, String name, String[] argsType, Class<?> returnType) {
        super(owner, name);
        this.argsType = Arrays.stream(argsType)
                .map(AsmUtil::getType).toArray(Type[]::new);
        this.returnType = returnType;
    }

    public Type[] getArgsType() {
        return argsType;
    }

    public Type getRetunrType() {
        return Type.getType(returnType);
    }

    public Type getMethodType() {
        return Type.getMethodType(getRetunrType(), getArgsType());
    }
}
