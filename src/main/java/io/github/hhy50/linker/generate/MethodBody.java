package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.asm.MethodBuilder;
import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

/**
 * The type Method body.
 */
public class MethodBody {
    private final MethodBuilder methodBuilder;

    private final MethodVisitor writer;
    private int lvbIndex;
    private final VarInst[] args;

    /**
     * Instantiates a new Method body.
     *
     * @param methodBuilder the method builder
     * @param mv            the method writer
     */
    public MethodBody(MethodBuilder methodBuilder, MethodVisitor mv) {
        this.methodBuilder = methodBuilder;

        Type[] argumentTypes = Type.getArgumentTypes(methodBuilder.getMethodDesc());
        this.writer = mv;
        this.lvbIndex = AsmUtil.calculateLvbOffset(methodBuilder.isStatic(), argumentTypes);
        this.args = new VarInst[argumentTypes.length];

        initArgsTable(argumentTypes);
    }

    private void initArgsTable(Type[] argumentTypes) {
        int index = 1;
        for (int i = 0; i < argumentTypes.length; i++) {
            args[i] = new ObjectVar(this, index++, argumentTypes[i]);
            if (argumentTypes[i].getSort() == Type.DOUBLE || argumentTypes[i].getSort() == Type.LONG) {
                index++;
            }
        }
    }

    /**
     * Append.
     *
     * @param interceptor the interceptor
     */
    @Deprecated
    public void append(Consumer<MethodVisitor> interceptor) {
        interceptor.accept(writer);
    }

    /**
     * Append.
     *
     * @param action the action
     */
    public void append(Action action) {
        action.apply(this);
    }

    /**
     * Get args var inst [ ].
     *
     * @return the var inst [ ]
     */
    public VarInst[] getArgs() {
        return args;
    }

    /**
     * Gets writer.
     *
     * @return the writer
     */
    public MethodVisitor getWriter() {
        return writer;
    }

    /**
     * New local var local var inst.
     *
     * @param type      the type
     * @param fieldName the field name
     * @param action    the action
     * @return the local var inst
     */
    public LocalVarInst newLocalVar(Type type, String fieldName, Action action) {
        LocalVarInst localVarInst = new LocalVarInst(this, lvbIndex++, type, fieldName);
        if (type.getSort() == Type.LONG || type.getSort() == Type.DOUBLE) {
            lvbIndex++;
        }
        if (action != null) {
            localVarInst.store(action);
        }
        return localVarInst;
    }

    /**
     * New local var local var inst.
     *
     * @param type   the type
     * @param action the action
     * @return the local var inst
     */
    public LocalVarInst newLocalVar(Type type, Action action) {
        return newLocalVar(type, null, action);
    }

    /**
     * Gets class builder.
     *
     * @return the class builder
     */
    public AsmClassBuilder getClassBuilder() {
        return methodBuilder.getClassBuilder();
    }

    /**
     * Gets method builder.
     * @return
     */
    public MethodDescriptor getMethodDescriptor() {
        return methodBuilder.getMethodDescriptor();
    }

    public void end() {
        this.writer.visitMaxs(0, 0);
    }
}
