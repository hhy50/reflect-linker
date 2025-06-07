package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.asm.MethodBuilder;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.ClassLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.TypedAction;
import io.github.hhy50.linker.generate.bytecode.block.BasicBlock;
import io.github.hhy50.linker.generate.bytecode.block.CodeBlock;
import io.github.hhy50.linker.generate.bytecode.block.CodeWriter;
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
    private final MethodBuilder builder;
    private final CodeWriter codeWriter;
    private final CodeBlock block;
    private int lvbIndex;
    private final VarInst[] args;
    private final Map<String, ClassLoadAction> classLoadCache;

    /**
     * Instantiates a new Method body.
     *
     * @param builder the method builder
     * @param mv            the method writer
     */
    public MethodBody(MethodBuilder builder, MethodVisitor mv) {
        Type[] argsType = builder.getDescriptor().getType().getArgumentTypes();

        this.builder = builder;
        this.codeWriter = new CodeWriter(mv);
        this.block = new BasicBlock(this.codeWriter);
        this.classLoadCache = new HashMap<>();
        this.args = new VarInst[argsType.length];
        initArgsTable(argsType);
    }

    private void initArgsTable(Type[] argsType) {
        int index = this.builder.isStatic() ? 0 : 1;
        for (int i = 0; i < argsType.length; i++) {
            this.args[i] = new ObjectVar(this.block, index++, argsType[i]);
            if (argsType[i].getSort() == Type.DOUBLE || argsType[i].getSort() == Type.LONG) {
                index++;
            }
        }
        this.lvbIndex = index;
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
        // 部分TypedAction可能拿不到早期的类型定义, 只有执行后才确定类型
        Type type = action.getType();
        return newLocalVar(type, name, action);
    }

    /**
     * Gets class builder.
     *
     * @return the class builder
     */
    public AsmClassBuilder getClassBuilder() {
        return builder.getClassBuilder();
    }

    /**
     * Gets method builder.
     *
     * @return the method builder
     */
    public MethodBuilder getBuilder() {
        return this.builder;
    }

    /**
     * End.
     */
    public void end() {
        AsmClassBuilder classBuilder = builder.getClassBuilder();
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
