package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
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
     * @param methodRef a {@link io.github.hhy50.linker.define.method.EarlyMethodRef} object.
     */
    public EarlyMethodInvoker(String implClass, EarlyMethodRef methodRef) {
        super(implClass, methodRef, methodRef.getMethodType());
    }

    /** {@inheritDoc} */
    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef owner = method.getOwner();
        Getter<?> getter = classImplBuilder.getGetter(owner.getUniqueName());
        getter.define(classImplBuilder);

        MethodBody clinit = classImplBuilder.getClinit();
        ClassTypeMember ownerType = classImplBuilder.defineClassTypeMember(method.getFullName()+"_lookup");
        ownerType.staticInit(clinit, getClassLoadAction(owner.getType()));

        // init methodHandle
        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getInvokerName(), methodType);
        initStaticMethodHandle(clinit, mhMember, ownerType, method.getName(), method.getMethodType(), method.isStatic());

        // 定义当前方法的invoker
        classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(classImplBuilder, mv, methodType);
                    VarInst result = null;
                    if (!method.isStatic()) {
                        VarInst objVar = getter.invoke(methodBody);
                        objVar.checkNullPointer(methodBody, objVar.getName());
                        result = mhMember.invokeInstance(methodBody, objVar, methodBody.getArgs());
                    } else {
                        result = mhMember.invokeStatic(methodBody, methodBody.getArgs());
                    }
                    if (result != null) {
                        result.load(methodBody);
                    }
                    AsmUtil.areturn(mv, methodType.getReturnType());
                });
    }

    /** {@inheritDoc} */
    @Override
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassTypeMember lookupClass, String fieldName, Type methodType, boolean isStatic) {
        String superClass = this.method.getSuperClass();
        boolean invokeSpecial = superClass != null;
        VarInst lookupVar = lookupClass.getLookup(clinit);

        MethodInvokeAction findXXX;
        Action argsType = Action.asArray(Type.getType(Class.class), Arrays.stream(methodType.getArgumentTypes()).map(LdcLoadAction::of).toArray(LdcLoadAction[]::new));
        if (invokeSpecial) {
            findXXX = new MethodInvokeAction(MethodHolder.LOOKUP_FIND_FINDSPECIAL).setArgs(
                    LdcLoadAction.of(AsmUtil.getType(superClass)),
                    LdcLoadAction.of(fieldName),
                    new MethodInvokeAction(MethodHolder.METHOD_TYPE).setArgs(LdcLoadAction.of(methodType.getReturnType()), argsType),
                    lookupClass
            );
        } else {
            findXXX = new MethodInvokeAction(MethodHolder.LOOKUP_FIND_FINDVIRTUAL).setArgs(
                    lookupClass,
                    LdcLoadAction.of(fieldName),
                    new MethodInvokeAction(MethodHolder.METHOD_TYPE).setArgs(LdcLoadAction.of(methodType.getReturnType()), argsType)
            );
        }
        mhMember.store(clinit, findXXX.setInstance(lookupVar));
    }
}
