package io.github.hhy.linker.generate.bytecode.vars;


import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.action.Action;
import io.github.hhy.linker.generate.bytecode.action.LoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * VarInstance
 * 生成可以复用的字节码
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

    public VarInst(int lvbIndex, Type type) {
        this.lvbIndex = lvbIndex;
        this.type = type;
    }

    /**
     * 检查是否为空， 如果变量为空就抛出空指针
     * <pre>
     *     if (var == null) {
     *         throw new NullPointerException(nullerr);
     *     }
     * </pre>
     */
    public void checkNullPointer(MethodBody methodBody, String nullerr) {
        if (type.getSort() > Type.DOUBLE) {
            this.ifNull(methodBody, Action.throwNullException(nullerr));
        }
    }

    public MethodInvokeAction getThisClass() {
        return new MethodInvokeAction(MethodHolder.OBJECT_GET_CLASS)
                .setInstance(this);
    }

    /**
     * load 到栈上
     *
     * @return
     */

    @Override
    public void load(MethodBody methodBody) {
        methodBody.append(mv -> mv.visitVarInsn(type.getOpcode(Opcodes.ILOAD), lvbIndex));
    }

    /**
     * store到局部变量表
     *
     * @return
     */
    public void store(MethodBody methodBody) {
        methodBody.append(mv -> {
            mv.visitVarInsn(type.getOpcode(Opcodes.ISTORE), lvbIndex);
        });
    }

    public void store(MethodBody body, Action action) {
        MethodVisitor mv = body.getWriter();
        action.apply(body);
        mv.visitVarInsn(type.getOpcode(Opcodes.ISTORE), lvbIndex);
    }

    public String getName() {
        return "slot["+lvbIndex+",type="+type.getClassName()+"]";
    }

    public Type getType() {
        return type;
    }
}
