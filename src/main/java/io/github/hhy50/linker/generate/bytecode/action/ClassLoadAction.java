package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.vars.ClassTypeVarInst;
import io.github.hhy50.linker.runtime.Runtime;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.util.Objects;

/**
 * The type Class load action.
 */
public class ClassLoadAction implements ClassTypeVarInst, LoadAction, TypedAction {
    /**
     * The Load action.
     */
    protected final Action loadAction;
    /**
     * The Is primitive.
     */
    protected boolean isPrimitive;

    /**
     * Instantiates a new Class load action.
     *
     * @param type the type
     */
    public ClassLoadAction(Type type) {
        Objects.requireNonNull(type);
        if (TypeUtil.isPrimitiveType(type)) {
            this.loadAction = LdcLoadAction.of(type);
            this.isPrimitive = true;
        } else {
            this.loadAction = LdcLoadAction.of(type.getClassName());
        }
    }

    /**
     * Instantiates a new Class load action.
     *
     * @param strloadAction the strload action
     */
    public ClassLoadAction(Action strloadAction) {
        Objects.requireNonNull(strloadAction);
        this.loadAction = strloadAction;
    }

    @Override
    public Action load() {
        if (this.isPrimitive) {
            return this.loadAction;
        }

        return body -> {
            AsmClassBuilder classBuilder = body.getClassBuilder();
            Action cl = LdcLoadAction.of(TypeUtil.getType(classBuilder.getClassName()))
                    .invokeMethod(MethodDescriptor.GET_CLASS_LOADER);
            body.append(new MethodInvokeAction(Runtime.GET_CLASS)
                    .setArgs(cl, loadAction));
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