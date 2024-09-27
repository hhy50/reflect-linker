package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.asm.AsmUtil;
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
    private final AsmClassBuilder classBuilder;
    private final MethodVisitor writer;
    private int lvbIndex;
    private VarInst[] args;

    /**
     * Instantiates a new Method body.
     *
     * @param classBuilder the class builder
     * @param mv           the mv
     * @param methodType   the method type
     */
    public MethodBody(AsmClassBuilder classBuilder, MethodVisitor mv, Type methodType) {
        this(classBuilder, mv, methodType, false);
    }

    /**
     * Instantiates a new Method body.
     *
     * @param classBuilder the class builder
     * @param mv           the mv
     * @param methodType   the method type
     * @param isStatic     the is static
     */
    public MethodBody(AsmClassBuilder classBuilder, MethodVisitor mv, Type methodType, boolean isStatic) {
        this.classBuilder = classBuilder;
        this.writer = mv;
        Type[] argumentTypes = methodType.getArgumentTypes();
        this.lvbIndex = AsmUtil.calculateLvbOffset(isStatic, argumentTypes);
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
        interceptor.accept(this.writer);
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
        return classBuilder;
    }
}
