package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import io.github.hhy50.linker.util.ClassUtil;
import org.objectweb.asm.Type;

/**
 * The type Setter.
 */
public class Setter extends FieldOpsMethodHandler {

    /**
     * The Field.
     */
    protected final FieldRef field;

    /**
     * Instantiates a new Setter.
     *
     * @param implClass the impl class
     * @param field     the field
     */
    public Setter(String implClass, FieldRef field) {
        super(field.getFullName(), MethodDescriptor.of(ClassUtil.className2path(implClass), "set_" + field.getFullName().replace('.', '_'),
                Type.getMethodType(Type.VOID_TYPE, field.getType())));
        this.field = field;
    }

    public void define0(InvokeClassImplBuilder classImplBuilder) {
        if (field instanceof RuntimeFieldRef) {
            this.lookupClass = classImplBuilder.defineLookupClass(fullName);
            super.defineRuntimeMethod(classImplBuilder, (RuntimeFieldRef) field);
        } else {
            super.defineMethod(classImplBuilder, (EarlyFieldRef) field);
        }
    }

//    @Override
//    public ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, ChainAction<VarInst[]> argsChainAction) {
//        if (super.inlineMhInvoker != null) {
//            methodBody.append(super.inlineMhInvoker.invoke(Args.loadArgs()).andThen(Actions.vreturn()));
//            return null;
//        }
//        methodBody.append(new MethodInvokeAction(descriptor)
//                .setInstance(LoadAction.LOAD0)
//                .setArgs(methodBody.getArgs()).andThen(Actions.vreturn()));
//        return null;
//    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, ChainAction<VarInst[]> argsChainAction) {
        if (super.inlineMhInvoker != null) {
            return super.inlineMhInvoker.invoke(varInstChain, argsChainAction);
        }
        return varInstChain.map(varInst -> Actions.newLocalVar(new SmartMethodInvokeAction(descriptor)
                .setInstance(LoadAction.LOAD0)
                .setArgs(varInst, argsChainAction)));
    }


    @Override
    protected Action initRuntimeMethodHandle(MethodHandleMember mhMember, ClassTypeMember lookupClass, VarInst objVar) {
        MethodInvokeAction findSetter = new MethodInvokeAction(Runtime.FIND_SETTER)
                .setArgs(lookupClass.getLookup(), lookupClass, LdcLoadAction.of(this.field.fieldName));
        return mhMember.store(findSetter);
    }

    @Override
    protected Action initStaticMethodHandle(MethodHandleMember mhMember, ClassTypeVarInst lookupClass, String fieldName, Type fieldType, boolean isStatic) {
        MethodInvokeAction findSetter = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATICSETTER : MethodDescriptor.LOOKUP_FINDSETTER)
                .setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, LdcLoadAction.of(fieldName), loadClass(fieldType));
        return mhMember.store(findSetter);
    }
}
