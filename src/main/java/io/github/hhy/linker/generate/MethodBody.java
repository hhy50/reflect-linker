package io.github.hhy.linker.generate;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.generate.bytecode.action.Action;
import io.github.hhy.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>MethodBody class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class MethodBody {
    private final InvokeClassImplBuilder classBuilder;
    private final MethodVisitor writer;
    private int lvbIndex;
    private VarInst[] args;

    /**
     * <p>Constructor for MethodBody.</p>
     *
     * @param classBuilder a {@link io.github.hhy.linker.generate.InvokeClassImplBuilder} object.
     * @param mv a {@link org.objectweb.asm.MethodVisitor} object.
     * @param methodType a {@link org.objectweb.asm.Type} object.
     */
    public MethodBody(InvokeClassImplBuilder classBuilder, MethodVisitor mv, Type methodType) {
        this.classBuilder = classBuilder;
        this.writer = mv;
        Type[] argumentTypes = methodType.getArgumentTypes();
        this.lvbIndex = AsmUtil.calculateLvbOffset(false, argumentTypes);
        this.args = new VarInst[argumentTypes.length];

        initArgsTable(argumentTypes);
    }

    private void initArgsTable(Type[] argumentTypes) {
        int index = 1;
        for (int i = 0; i < argumentTypes.length; i++) {
            args[i] = new ObjectVar(index++, argumentTypes[i]);
            if (argumentTypes[i].getSort() == Type.DOUBLE || argumentTypes[i].getSort() == Type.LONG) {
                index++;
            }
        }
    }

    /**
     * <p>append.</p>
     *
     * @param interceptor a {@link java.util.function.Consumer} object.
     */
    @Deprecated
    public void append(Consumer<MethodVisitor> interceptor) {
        interceptor.accept(this.writer);
    }

    /**
     * <p>append.</p>
     *
     * @param interceptor a {@link java.util.function.Supplier} object.
     */
    public void append(Supplier<Action> interceptor) {
        Action action = interceptor.get();
        action.apply(this);
    }

    /**
     * 根据参数声明的顺序， 获取第几个参数
     *
     * @param i a int.
     * @return a {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} object.
     */
    public VarInst getArg(int i) {
        return args[i];
    }

    /**
     * <p>Getter for the field <code>args</code>.</p>
     *
     * @return an array of {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} objects.
     */
    public VarInst[] getArgs() {
        return args;
    }

    /**
     * <p>Getter for the field <code>writer</code>.</p>
     *
     * @return a {@link org.objectweb.asm.MethodVisitor} object.
     */
    public MethodVisitor getWriter() {
        return writer;
    }

    /**
     * <p>newLocalVar.</p>
     *
     * @param type a {@link org.objectweb.asm.Type} object.
     * @param fieldName a {@link java.lang.String} object.
     * @param action a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.vars.LocalVarInst} object.
     */
    public LocalVarInst newLocalVar(Type type, String fieldName, Action action) {
        LocalVarInst localVarInst = new LocalVarInst(lvbIndex++, type, fieldName);
        if (action != null) {
            this.append(() -> localVarInst.store(action));
        }
        return localVarInst;
    }

    /**
     * <p>newLocalVar.</p>
     *
     * @param type a {@link org.objectweb.asm.Type} object.
     * @param action a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.vars.LocalVarInst} object.
     */
    public LocalVarInst newLocalVar(Type type, Action action) {
        return newLocalVar(type, null, action);
    }

    /**
     * <p>Getter for the field <code>classBuilder</code>.</p>
     *
     * @return a {@link io.github.hhy.linker.generate.InvokeClassImplBuilder} object.
     */
    public InvokeClassImplBuilder getClassBuilder() {
        return classBuilder;
    }
}
