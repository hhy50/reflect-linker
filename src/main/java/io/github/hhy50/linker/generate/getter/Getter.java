package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldIndexRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.FieldOpsMethodHandler;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import io.github.hhy50.linker.util.ClassUtil;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;


/**
 * The type Getter.
 */
public class Getter extends FieldOpsMethodHandler {

    /**
     * The Field.
     */
    protected final FieldRef field;
    /**
     * The Impl class.
     */
    protected final String implClass;

    /**
     * Instantiates a new Getter.
     *
     * @param implClass the impl class
     * @param field     the field
     */
    public Getter(String implClass, FieldRef field) {
        super(field.getGetterName(), MethodDescriptor.of(ClassUtil.className2path(implClass), "get_"+field.getUniqueName(),
                Type.getMethodType(field.getType())));
        this.implClass = implClass;
        this.field = field;
    }

    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        if (field instanceof RuntimeFieldRef) {
            super.defineRuntimeMethod(classImplBuilder, (RuntimeFieldRef) field);
        } else if (field instanceof EarlyFieldRef) {
            super.defineMethod(classImplBuilder, (EarlyFieldRef) field);
        } else if (field instanceof FieldIndexRef) {
            this.defineIndexMethod(classImplBuilder, (FieldIndexRef) field);
        }
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        MethodInvokeAction invoker = new MethodInvokeAction(descriptor)
                .setInstance(LoadAction.LOAD0);
        return methodBody.newLocalVar(descriptor.getReturnType(), field.fieldName, invoker);
    }

    @Override
    protected void initRuntimeMethodHandle(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar) {
        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_GETTER)
                .setArgs(lookupClass.getLookup(methodBody), lookupClass, LdcLoadAction.of(field.fieldName));
        mhMember.store(methodBody, findGetter);
    }

    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassLoadAction lookupClass, String fieldName, Type fieldType, boolean isStatic) {
        MethodInvokeAction findGetter = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATICGETTER : MethodDescriptor.LOOKUP_FINDGETTER);
        findGetter.setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, LdcLoadAction.of(fieldName), loadClass(fieldType));
        mhMember.store(clinit, findGetter);
    }

    private void defineIndexMethod(InvokeClassImplBuilder classImplBuilder, FieldIndexRef field) {
        FieldRef prev = field.getPrev();
        Getter getter = classImplBuilder.getGetter(prev.getUniqueName());
        getter.define(classImplBuilder);

        Class<?> actualType = prev.getActualType();
        List<Object> index = field.getIndex();
        if (actualType.isArray() && index.size() == 1 && index.get(0) instanceof Integer) {
            classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                    .intercept(ChainAction.of(getter::invoke)
                            .then(varInst -> new ArrayIndex(varInst, (Integer) index.get(0)))
                            .andThen(Actions.areturn(descriptor.getReturnType()))
                    );
            return;
        }
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, descriptor.getMethodName(), descriptor.getType(), null)
                .intercept(ChainAction.of(getter::invoke)
                        .map(varInst -> Methods.invoke(RuntimeUtil.INDEX_VALUE).setArgs(varInst,
                                Actions.asList(index.stream().map(LdcLoadAction::of)
                                        .map(inst -> {
                                            if (TypeUtil.isPrimitiveType(inst.getType())) {
                                                return new BoxAction(inst);
                                            }
                                            return inst;
                                        })
                                        .toArray(Action[]::new))))
                        .andThen(Actions.areturn(descriptor.getReturnType()))
                );
    }
}
