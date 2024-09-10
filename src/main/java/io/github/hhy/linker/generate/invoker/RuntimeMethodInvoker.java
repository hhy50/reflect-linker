package io.github.hhy.linker.generate.invoker;

import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.define.method.RuntimeMethodRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.Action;
import io.github.hhy.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.generate.getter.Getter;
import io.github.hhy.linker.runtime.Runtime;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;


public class RuntimeMethodInvoker extends Invoker<RuntimeMethodRef> {

    public RuntimeMethodInvoker(String implClass, RuntimeMethodRef methodRef) {
        super(implClass, methodRef, methodRef.getMethodType());
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        FieldRef owner = method.getOwner();
        Getter<?> ownerGetter = classImplBuilder.defineGetter(owner.getUniqueName(), owner);
        ownerGetter.define(classImplBuilder);

        // 先定义上一层字段的lookup
        LookupMember lookupMember = classImplBuilder.defineRuntimeLookup(owner);
        // 定义当前字段的mh
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(method.getInvokerName(), methodType);
        // 定义当前方法的invoker
        classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(classImplBuilder, mv, methodType);
                    VarInst ownerVar = ownerGetter.invoke(methodBody);

                    if (!ownerGetter.isTargetGetter()) {
                        // 校验lookup和mh
                        LookupMember prevLookup = ownerGetter.getLookupMember();
                        staticCheckLookup(methodBody, prevLookup, lookupMember, ownerVar, owner);
                        checkLookup(methodBody, lookupMember, mhMember, ownerVar);
                    }
                    checkMethodHandle(methodBody, lookupMember, mhMember, ownerVar);

                    // mh.invoke(obj)
                    VarInst result = mhMember.invoke(methodBody, ownerVar, methodBody.getArgs());
                    result.returnThis(methodBody);
                });
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        String superClass = method.getSuperClass();
        Action superClassLoad = superClass != null ? LdcLoadAction.of(superClass) : Action.loadNull();
        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_METHOD)
                .setArgs(lookupMember,
                        LdcLoadAction.of(method.getName()),
                        superClassLoad,
                        Action.asArray(Type.getType(String.class), Arrays.stream(method.getArgsType())
                                .map(Type::getClassName).map(LdcLoadAction::of).toArray(Action[]::new))
                );
        mhMember.store(methodBody, findGetter);
    }
}
