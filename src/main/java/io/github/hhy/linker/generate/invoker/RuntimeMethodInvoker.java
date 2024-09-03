package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.define.method.RuntimeMethodRef;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.Action;
import io.github.hhy.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.generate.getter.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;

import static io.github.hhy.linker.generate.bytecode.action.Action.asArray;

public class RuntimeMethodInvoker extends Invoker<RuntimeMethodRef> {

    public RuntimeMethodInvoker(String implClass, RuntimeMethodRef methodRef) {
        super(implClass, methodRef, null);
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef owner = method.getOwner();
        Getter<?> getter = classImplBuilder.defineGetter(owner.getUniqueName(), owner);
        getter.define(classImplBuilder);

        // 先定义上一层字段的lookup
        LookupMember lookupMember = classImplBuilder.defineLookup(owner);
        // 定义当前字段的mh
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(owner.getGetterName(), methodType);

        // 定义当前方法的invoker
        classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(mv, methodType);
                    VarInst objVar = getter.invoke(methodBody);

                    if (!lookupMember.isTargetLookup()) {
                        // 校验lookup和mh
                        LookupMember preLookup = classImplBuilder.defineLookup(owner.getPrev());
                        staticCheckLookup(methodBody, preLookup, lookupMember, objVar, owner.getPrev());
                        checkLookup(methodBody, lookupMember, mhMember, objVar);
                    }
                    checkMethodHandle(methodBody, lookupMember, mhMember, objVar);

                    // mh.invoke(obj)
                    VarInst result = mhMember.invoke(methodBody, objVar);
                    result.returnThis(methodBody);
                });
    }

    @Override
    protected void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember, Type ownerType, String fieldName, Type methodType, boolean isStatic) {
        String superClass = this.method.getSuperClass();
        boolean invokeSpecial = superClass != null;
        MethodBody clinit = classImplBuilder.getClinit();

        MethodInvokeAction findXXX;
        Action argsType = asArray(Type.getType(Class.class), Arrays.stream(methodType.getArgumentTypes()).map(LdcLoadAction::of).toArray(LdcLoadAction[]::new));
        if (invokeSpecial) {
            findXXX = new MethodInvokeAction(MethodHolder.LOOKUP_FIND_FINDSPECIAL).setArgs(
                    LdcLoadAction.of(Type.getType(AsmUtil.toTypeDesc(superClass))),
                    LdcLoadAction.of(fieldName),
                    new MethodInvokeAction(MethodHolder.METHOD_TYPE).setArgs(LdcLoadAction.of(methodType.getReturnType()), argsType),
                    LdcLoadAction.of(ownerType)
            );
        } else {
            findXXX = new MethodInvokeAction(MethodHolder.LOOKUP_FIND_FINDVIRTUAL).setArgs(
                    LdcLoadAction.of(ownerType),
                    LdcLoadAction.of(fieldName),
                    new MethodInvokeAction(MethodHolder.METHOD_TYPE).setArgs(LdcLoadAction.of(methodType.getReturnType()), argsType)
            );
        }
        mhMember.store(clinit, findXXX.setInstance(lookupMember));
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        // mh = Runtime.findGetter(lookup, "field");
//        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_GETTER)
//                .setArgs(lookupMember, LdcLoadAction.of(field.fieldName));
//        mhMember.store(methodBody, findGetter);
    }
}
