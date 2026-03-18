package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * The type Wrap type action.
 */
public class BoxAction extends VarInst {
    private final TypedAction obj;
    private final Type wrapperType;

    /**
     * Instantiates a new Wrap type action.
     *
     * @param obj         the obj
     * @param wrapperType the wrapper type
     */
    public BoxAction(TypedAction obj, Type wrapperType) {
        this.obj = obj;
        this.wrapperType = wrapperType;
    }

    /**
     * Instantiates a new Wrap type action.
     *
     * @param obj the obj
     */
    public BoxAction(TypedAction obj) {
        this.obj = obj;
        this.wrapperType = TypeUtil.getBoxType(obj.getType());
    }

    @Override
    public Action load() {
        if (this.wrapperType != null) {
            if (this.wrapperType.equals(ObjectVar.TYPE)) {
                return Actions.withVisitor(obj, c -> c.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "wrap",
                        "(" + obj.getType().getDescriptor() + ")Ljava/lang/Object;", false));
            }
            return Actions.withVisitor(obj, c -> c.visitMethodInsn(Opcodes.INVOKESTATIC, this.wrapperType.getInternalName(), "valueOf",
                    Type.getMethodDescriptor(this.wrapperType, obj.getType()), false));
        }
        return obj;
    }

    @Override
    public Type getType() {
        if (this.wrapperType != null) {
            return this.wrapperType;
        }
        return obj.getType();
    }
}
