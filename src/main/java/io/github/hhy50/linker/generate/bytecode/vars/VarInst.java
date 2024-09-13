package io.github.hhy50.linker.generate.bytecode.vars;


import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.action.TypeCastAction;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * VarInstance
 * 生成可以复用的字节码
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public abstract class VarInst implements LoadAction {


    /**
     * 当前变量在局部变量表中的索引
     */
    private final int lvbIndex;

    /**
     * 类型
     */
    protected Type type;

    /**
     * <p>Constructor for VarInst.</p>
     *
     * @param lvbIndex a int.
     * @param type a {@link org.objectweb.asm.Type} object.
     */
    public VarInst(int lvbIndex, Type type) {
        this.lvbIndex = lvbIndex;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     *
     * load 到栈上
     */
    @Override
    public void load(MethodBody methodBody) {
        methodBody.append(mv -> mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), lvbIndex));
    }

    /**
     * 检查是否为空， 如果变量为空就抛出空指针
     * <pre>
     *     if (var == null) {
     *         throw new NullPointerException(nullerr);
     *     }
     * </pre>
     *
     * @param methodBody a {@link MethodBody} object.
     * @param nullerr a {@link java.lang.String} object.
     */
    public void checkNullPointer(MethodBody methodBody, String nullerr) {
        if (type.getSort() > Type.DOUBLE) {
            this.ifNull(methodBody, Action.throwNullException(nullerr));
        }
    }

    /**
     * <p>getThisClass.</p>
     *
     * @return a {@link MethodInvokeAction} object.
     */
    public MethodInvokeAction getThisClass() {
        return new MethodInvokeAction(MethodHolder.OBJECT_GET_CLASS)
                .setInstance(this);
    }

    /**
     * store到局部变量表
     *
     * @param action a {@link Action} object.
     * @return a {@link Action} object.
     */
    public Action store(Action action) {
        return body -> {
            MethodVisitor mv = body.getWriter();
            action.apply(body);
            mv.visitVarInsn(type.getOpcode(Opcodes.ISTORE), lvbIndex);
        };
    }

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return "slot["+lvbIndex+",type="+type.getClassName()+"]";
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link org.objectweb.asm.Type} object.
     */
    public Type getType() {
        return type;
    }

    /**
     * <p>returnThis.</p>
     *
     * @param methodBody a {@link MethodBody} object.
     */
    public void returnThis(MethodBody methodBody) {
        load(methodBody);
        AsmUtil.areturn(methodBody.getWriter(), type);
    }

    /**
     * <p>getTarget.</p>
     *
     * @param providerType a {@link org.objectweb.asm.Type} object.
     * @return a {@link Action} object.
     */
    public Action getTarget(Type providerType) {
        Type defaultType = Type.getType(DefaultTargetProviderImpl.class);
        if (this.type.equals(defaultType)) {
            return Action.multi(
                    new MethodInvokeAction(MethodHolder.DEFAULT_PROVIDER_GET_TARGET).setInstance(this),
                    new TypeCastAction(Action.stackTop(), providerType)
            );
        } else {
            return Action.multi(
                    new TypeCastAction(this, defaultType)
                            .onAfter(new MethodInvokeAction(MethodHolder.DEFAULT_PROVIDER_GET_TARGET)
                                    .setInstance(Action.stackTop())),
                    new TypeCastAction(Action.stackTop(), providerType)
            );
        }
    }
}
