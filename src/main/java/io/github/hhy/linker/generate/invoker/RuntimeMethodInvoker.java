//package io.github.hhy.linker.generate.invoker;
//
//import io.github.hhy.linker.asm.AsmUtil;
//import io.github.hhy.linker.define.field.EarlyFieldRef;
//import io.github.hhy.linker.define.method.MethodRef;
//import io.github.hhy.linker.entity.MethodHolder;
//import io.github.hhy.linker.generate.InvokeClassImplBuilder;
//import io.github.hhy.linker.generate.MethodBody;
//import io.github.hhy.linker.generate.bytecode.LookupMember;
//import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
//import io.github.hhy.linker.generate.bytecode.action.Action;
//import io.github.hhy.linker.generate.bytecode.action.LdcLoadAction;
//import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
//import io.github.hhy.linker.generate.bytecode.vars.VarInst;
//import io.github.hhy.linker.generate.getter.Getter;
//import org.objectweb.asm.Opcodes;
//import org.objectweb.asm.Type;
//
//import java.util.Arrays;
//
//import static io.github.hhy.linker.generate.bytecode.action.Action.asArray;
//
//public class RuntimeMethodInvoker extends Invoker<MethodRef> {
//    private Type methodType;
//
//    public RuntimeMethodInvoker(String implClass, MethodRef methodRef) {
//        super(implClass, methodRef, methodRef.getMethodType());
////        this.methodType = methodRef.getMethodType();
//    }
//
//    @Override
//    protected void define0(InvokeClassImplBuilder classImplBuilder) {
//        EarlyFieldRef owner = (EarlyFieldRef) method.getOwner();
//        Getter<?> getter = classImplBuilder.defineGetter(owner.getUniqueName(), owner);
//        getter.define(classImplBuilder);
//
//        MethodBody clinit = classImplBuilder.getClinit();
//        // init lookup
//        LookupMember lookupMember = classImplBuilder.defineLookup(owner);
//        lookupMember.staticInit(clinit);
//
//        // init methodHandle
//        MethodHandleMember mhMember = classImplBuilder.defineStaticMethodHandle(method.getFullName(), this.methodType);
//        initStaticMethodHandle(classImplBuilder, mhMember, lookupMember, owner.getType(), method.getName(), methodType, method.isStatic());
//
//        // 定义当前方法的invoker
//        classImplBuilder
//                .defineMethod(Opcodes.ACC_PUBLIC, methodHolder.getMethodName(), methodHolder.getDesc(), null, "")
//                .accept(mv -> {
//                    MethodBody methodBody = new MethodBody(mv, methodType);
//                    VarInst objVar = getter.invoke(methodBody);
//                    if (!method.isStatic()) {
//                        objVar.checkNullPointer(methodBody, objVar.getName());
//                    }
//
//                    // mh.invoke(obj)
//                    VarInst result = method.isStatic() ? mhMember.invokeStatic(methodBody, methodBody.getArgs()) : mhMember.invokeInstance(methodBody, objVar, methodBody.getArgs());
//                    if (result != null) {
//                        result.load(methodBody);
//                    }
//                    AsmUtil.areturn(mv, methodType.getReturnType());
//                });
//    }
//
//    @Override
//    protected void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember, Type ownerType, String fieldName, Type methodType, boolean isStatic) {
//        Class<?> superClass = this.method.getSuperClass();
//        boolean invokeSpecial = superClass != null;
//        MethodBody clinit = classImplBuilder.getClinit();
//
//        MethodInvokeAction findXXX;
//        Action argsType = asArray(Type.getType(Class.class), Arrays.stream(methodType.getArgumentTypes()).map(LdcLoadAction::of).toArray(LdcLoadAction[]::new));
//        if (invokeSpecial) {
//            findXXX = new MethodInvokeAction(MethodHolder.LOOKUP_FIND_FINDSPECIAL).setArgs(
//                    LdcLoadAction.of(Type.getType(superClass)),
//                    LdcLoadAction.of(fieldName),
//                    new MethodInvokeAction(MethodHolder.METHOD_TYPE).setArgs(LdcLoadAction.of(methodType.getReturnType()), argsType),
//                    LdcLoadAction.of(ownerType)
//            );
//        } else {
//            findXXX = new MethodInvokeAction(MethodHolder.LOOKUP_FIND_FINDVIRTUAL).setArgs(
//                    LdcLoadAction.of(ownerType),
//                    LdcLoadAction.of(fieldName),
//                    new MethodInvokeAction(MethodHolder.METHOD_TYPE).setArgs(LdcLoadAction.of(methodType.getReturnType()), argsType)
//            );
//        }
//        mhMember.store(clinit, findXXX.setInstance(lookupMember));
//    }
//
//    @Override
//    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
//        // mh = Runtime.findGetter(lookup, "field");
////        MethodInvokeAction findGetter = new MethodInvokeAction(Runtime.FIND_GETTER)
////                .setArgs(lookupMember, LdcLoadAction.of(field.fieldName));
////        mhMember.store(methodBody, findGetter);
//    }
//}
