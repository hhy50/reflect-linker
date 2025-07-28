package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.asm.MethodBuilder;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.ClassLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.TypedAction;
import io.github.hhy50.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The type Method body.
 */
public class MethodBody {
    private final MethodBuilder methodBuilder;

    private final MethodVisitor writer;
    private int lvbIndex;
    private final VarInst[] args;
    private final Map<String, ClassLoadAction> classLoadCache;

    /**
     * Instantiates a new Method body.
     *
     * @param methodBuilder the method builder
     * @param mv            the method writer
     */
    public MethodBody(MethodBuilder methodBuilder, MethodVisitor mv) {
        this.methodBuilder = methodBuilder;

        Type[] argumentTypes = methodBuilder.getDescriptor().getType().getArgumentTypes();
        this.writer = mv;
        this.lvbIndex = AsmUtil.calculateLvbOffset(methodBuilder.isStatic(), argumentTypes);
        this.args = new VarInst[argumentTypes.length];
        this.classLoadCache = new HashMap<>();
        initArgsTable(argumentTypes);
    }

    private void initArgsTable(Type[] argumentTypes) {
        int index = this.methodBuilder.isStatic() ? 0 : 1;
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
        if (action != null) localVarInst.store(action);
        return localVarInst;
    }

    /**
     * New local var local var inst.
     *
     * @param action the action
     * @return local var inst
     */
    public LocalVarInst newLocalVar(TypedAction action) {
        return newLocalVar((String) null, action);
    }

    /**
     * New local var local var inst.
     *
     * @param name   the name
     * @param action the action
     * @return local var inst
     */
    public LocalVarInst newLocalVar(String name, TypedAction action) {
        Type type = action.getType();
        return newLocalVar(type, name, action);
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
     *
     * @return the method builder
     */
    public MethodBuilder getMethodBuilder() {
        return this.methodBuilder;
    }

    /**
     * End.
     */
    public void end() {
        AsmClassBuilder classBuilder = methodBuilder.getClassBuilder();
        if (classBuilder.isAutoCompute()) {
            this.writer.visitMaxs(0, 0);
        }
        this.writer.visitEnd();
    }

    /**
     * Gets class obj cache.
     *
     * @param type the type
     * @return the class obj cache
     */
    public ClassLoadAction getClassObjCache(Type type) {
        return this.classLoadCache.get(type.getClassName());
    }

    /**
     * Put class obj cache.
     *
     * @param type      the type
     * @param classload the clazz var
     */
    public void putClassObjCache(Type type, ClassLoadAction classload) {
        this.classLoadCache.put(type.getClassName(), classload);
    }
}
