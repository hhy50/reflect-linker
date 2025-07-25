package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.MethodTmpFieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.FieldOpsMethodHandler;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.ClassLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import io.github.hhy50.linker.util.ClassUtil;
import org.objectweb.asm.Type;


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
        } else if (field instanceof MethodTmpFieldRef) {
            super.defineInvokeMethod(classImplBuilder, ((MethodTmpFieldRef) field).getMethodRef());
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
}
