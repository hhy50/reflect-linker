package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.LookupMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;

/**
 * <p>EarlyMethodInvoker class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class EarlyMethodInvoker extends Invoker<EarlyMethodRef> {

    /**
     * <p>Constructor for EarlyMethodInvoker.</p>
     *
     * @param implClass a {@link java.lang.String} object.
     * @param methodRef a {@link EarlyMethodRef} object.
     */
    public EarlyMethodInvoker(String implClass, EarlyMethodRef methodRef) {
        super(implClass, methodRef, methodRef.getMethodType());
    }

    /** {@inheritDoc} */
    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        EarlyFieldRef owner = (EarlyFieldRef) method.getOwner();
        Getter<?> getter = classImplBuilder.defineGetter(owner.getUniqueName(), owner);
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();
        Type ownerType = Type.getType(owner.getClassType());

        // init lookup
        LookupMember lookupMember = classImplBuilder.defineTypedLookup(ownerType.getClassName());
        lookupMember.staticInit(clinit, getClassLoadAction(ownerType));

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getInvokerName(), methodType);
        initStaticMethodHandle(classImplBuilder, mhMember, lookupMember, ownerType, method.getName(), method.getMethodType(), method.isStatic());

        // 定义当前方法的invoker
        classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(classImplBuilder, mv, methodType);
                    VarInst objVar = getter.invoke(methodBody);
                    if (!method.isStatic()) {
                        objVar.checkNullPointer(methodBody, objVar.getName());
                    }

                    // mh.invoke(obj)
                    VarInst result = method.isStatic() ? mhMember.invokeStatic(methodBody, methodBody.getArgs()) : mhMember.invokeInstance(methodBody, objVar, methodBody.getArgs());
                    if (result != null) {
                        result.load(methodBody);
                    }
                    AsmUtil.areturn(mv, methodType.getReturnType());
                });
    }

    /** {@inheritDoc} */
    @Override
    protected void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember, Type ownerType, String fieldName, Type methodType, boolean isStatic) {
        String superClass = this.method.getSuperClass();
        boolean invokeSpecial = superClass != null;
        MethodBody clinit = classImplBuilder.getClinit();

        MethodInvokeAction findXXX;
        Action argsType = Action.asArray(Type.getType(Class.class), Arrays.stream(methodType.getArgumentTypes()).map(LdcLoadAction::of).toArray(LdcLoadAction[]::new));
        if (invokeSpecial) {
            findXXX = new MethodInvokeAction(MethodHolder.LOOKUP_FIND_FINDSPECIAL).setArgs(
                    LdcLoadAction.of(AsmUtil.getType(superClass)),
                    LdcLoadAction.of(fieldName),
                    new MethodInvokeAction(MethodHolder.METHOD_TYPE).setArgs(LdcLoadAction.of(methodType.getReturnType()), argsType),
                    getClassLoadAction(ownerType)
            );
        } else {
            findXXX = new MethodInvokeAction(MethodHolder.LOOKUP_FIND_FINDVIRTUAL).setArgs(
                    getClassLoadAction(ownerType),
                    LdcLoadAction.of(fieldName),
                    new MethodInvokeAction(MethodHolder.METHOD_TYPE).setArgs(LdcLoadAction.of(methodType.getReturnType()), argsType)
            );
        }
        mhMember.store(clinit, findXXX.setInstance(lookupMember));
    }
}
