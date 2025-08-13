package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.SmartMethodDescriptor;
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
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Type;

import java.util.function.Consumer;


/**
 * The type Getter.
 */
public class Getter extends FieldOpsMethodHandler {

    protected String fieldName;
    protected Consumer<InvokeClassImplBuilder> define0;

    /**
     * Instantiates a new Getter.
     *
     * @param field the field
     */
    public Getter(FieldRef field) {
        super(field.getGetterName(), new SmartMethodDescriptor("get_"+field.getUniqueName(),
                Type.getMethodType(field.getType())));
        if (field instanceof RuntimeFieldRef) {
            define0 = (builder) -> {
                this.lookupClass = builder.defineLookupClass(field.getUniqueName());
                super.defineRuntimeMethod(builder, (RuntimeFieldRef) field);
            };
        } else if (field instanceof EarlyFieldRef) {
            define0 = (builder) -> super.defineMethod(builder, (EarlyFieldRef) field);
        } else if (field instanceof FieldIndexRef) {
            define0 = (builder) -> super.defineIndexMethod(builder, (FieldIndexRef) field);
        }
        this.fieldName = field.fieldName;
    }

    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        this.define0.accept(classImplBuilder);
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        Action invoker;
        if (super.inlineMhInvoker != null) {
            invoker = super.inlineMhInvoker.invoke();
        } else {
            invoker = new SmartMethodInvokeAction(descriptor)
                    .setInstance(LoadAction.LOAD0);
        }
        return methodBody.newLocalVar(descriptor.getReturnType(), fieldName, invoker);
    }

    @Override
    protected void initRuntimeMethodHandle(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar) {
        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_GETTER)
                .setArgs(lookupClass.getLookup(methodBody), lookupClass, LdcLoadAction.of(fieldName));
        mhMember.store(methodBody, findGetter);
    }

    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassLoadAction lookupClass, String fieldName, Type fieldType, boolean isStatic) {
        MethodInvokeAction findGetter = new MethodInvokeAction(isStatic ? MethodDescriptor.LOOKUP_FINDSTATICGETTER : MethodDescriptor.LOOKUP_FINDGETTER);
        findGetter.setInstance(lookupClass.getLookup())
                .setArgs(lookupClass, LdcLoadAction.of(fieldName), loadClass(fieldType));
        mhMember.store(clinit, findGetter);
    }

    public static Action checkNull(FieldRef field, VarInst varInst) {
        if (field.isNullable()) {
            return Actions.empty();
        }
        return varInst.checkNullPointer();
    }
}
