package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * The type Wrap type action.
 */
public class BoxAction implements LoadAction, TypedAction {
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
     * @param obj         the obj
     */
    public BoxAction(TypedAction obj) {
        this.obj = obj;
        this.wrapperType = TypeUtil.getBoxType(obj.getType());
    }

    @Override
    public void load(MethodBody body) {
        obj.apply(body);

        Type type = obj.getType();
        if (TypeUtil.isPrimitiveType(type)) {
            MethodVisitor mv = body.getWriter();
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "wrap", "("+obj.getType().getDescriptor()+")Ljava/lang/Object;", false);
        }
    }

    @Override
    public Type getType() {
        if (this.wrapperType != null) {
            return this.wrapperType;
        }
        return ObjectVar.TYPE;
    }
}
