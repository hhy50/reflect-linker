package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.vars.ClassTypeVarInst;
import io.github.hhy50.linker.runtime.Runtime;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.util.Objects;

public class ClassLoadAction implements ClassTypeVarInst, LoadAction, TypedAction {
    protected final Type type;

    public ClassLoadAction(Type type) {
        Objects.requireNonNull(type);
        this.type = type;
    }


    @Override
    public Action load() {
        if (TypeUtil.isPrimitiveType(type)) {
            return LdcLoadAction.of(type);
        }

        return body -> {
            AsmClassBuilder classBuilder = body.getClassBuilder();
            Action cl = LdcLoadAction.of(TypeUtil.getType(classBuilder.getClassName()))
                    .invokeMethod(MethodDescriptor.GET_CLASS_LOADER);
            body.append(new MethodInvokeAction(Runtime.GET_CLASS)
                    .setArgs(cl, LdcLoadAction.of(type.getClassName())));
        };
    }


    @Override
    public Type getType() {
        return Type.getType(Class.class);
    }

    @Override
    public Action getLookup() {
        return new MethodInvokeAction(Runtime.LOOKUP).setArgs(this);
    }
}