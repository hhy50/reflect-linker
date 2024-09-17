package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.ConditionJumpAction;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.generate.bytecode.action.Condition.*;

/**
 * <p>Abstract MethodHandle class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public abstract class MethodHandle {

    protected boolean defined = false;

    /**
     * <p>define.</p>
     *
     * @param classImplBuilder a {@link io.github.hhy50.linker.generate.InvokeClassImplBuilder} object.
     */
    public final void define(InvokeClassImplBuilder classImplBuilder) {
        if (this.defined) return;
        define0(classImplBuilder);
        this.defined = true;
    }

    /**
     * <p>define0.</p>
     *
     * @param classImplBuilder a {@link io.github.hhy50.linker.generate.InvokeClassImplBuilder} object.
     */
    protected void define0(InvokeClassImplBuilder classImplBuilder) {

    }

    /**
     * 调用 mh.invoke();
     *
     * @param methodBody a {@link io.github.hhy50.linker.generate.MethodBody} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} object.
     */
    public abstract VarInst invoke(MethodBody methodBody);

    /**
     * 初始化静态 methodhandle
     *
     * @param clinit      a {@link io.github.hhy50.linker.generate.MethodBody} object.
     * @param mhMember    a {@link io.github.hhy50.linker.generate.bytecode.MethodHandleMember} object.
     * @param lookupClass a {@link org.objectweb.asm.Type} object.
     * @param fieldName   a {@link java.lang.String} object.
     * @param methodType  a {@link org.objectweb.asm.Type} object.
     * @param isStatic    a boolean.
     */
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassTypeMember lookupClass, String fieldName, Type methodType, boolean isStatic) {

    }

    /**
     * mh 重新赋值字节码逻辑
     * <pre>
     *     mh = Runtime.findGetter(lookup, "c");
     * </pre>
     *
     * @param methodBody a {@link io.github.hhy50.linker.generate.MethodBody} object.
     * @param mhMember   a {@link io.github.hhy50.linker.generate.bytecode.MethodHandleMember} object.
     * @param objVar     a {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} object.
     * @param lookupClass a {@link io.github.hhy50.linker.generate.bytecode.ClassTypeMember} object.
     */
    protected abstract void mhReassign(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar);


//    protected void checkLookClass(MethodBody body, ClassTypeMember lookupClass, VarInst varInst, ClassTypeMember prevLookup) {
//        body.append(() -> new ConditionJumpAction(
//                any(
//                        isNull(lookupClass),
//                        must(notNull(varInst), notEq(varInst.getThisClass(), lookupClass))
//                ),
//                new ConditionJumpAction(notNull(varInst),
//                        (__) -> lookupClass.store(body, varInst.getThisClass()),
//                        (__) -> lookupClass.store(body, new MethodInvokeAction(Runtime.FIND_FIELD).setArgs(prevLookup, LdcLoadAction.of(prevLookup.getFieldName())))),
//                null
//        ));
//    }

    /**
     * <p>checkLookClass.</p>
     *
     * @param body a {@link io.github.hhy50.linker.generate.MethodBody} object.
     * @param lookupClass a {@link io.github.hhy50.linker.generate.bytecode.ClassTypeMember} object.
     * @param varInst a {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} object.
     */
    protected void checkLookClass(MethodBody body, ClassTypeMember lookupClass, VarInst varInst) {
        body.append(() -> new ConditionJumpAction(
                must(notNull(varInst),
                        any(isNull(lookupClass), notEq(varInst.getThisClass(), lookupClass))),
                (__) -> lookupClass.store(__, varInst.getThisClass()),
                null
        ));
    }

    /**
     * <p>staticCheckClass.</p>
     *
     * @param body a {@link io.github.hhy50.linker.generate.MethodBody} object.
     * @param lookupClass a {@link io.github.hhy50.linker.generate.bytecode.ClassTypeMember} object.
     * @param prevFieldName a {@link java.lang.String} object.
     * @param prevLookup a {@link io.github.hhy50.linker.generate.bytecode.ClassTypeMember} object.
     */
    protected void staticCheckClass(MethodBody body, ClassTypeMember lookupClass, String prevFieldName, ClassTypeMember prevLookup) {
        body.append(() -> new ConditionJumpAction(
                isNull(lookupClass),
                (__) -> lookupClass.store(__, new MethodInvokeAction(Runtime.FIND_FIELD).setArgs(prevLookup, LdcLoadAction.of(prevFieldName))),
                null
        ));
    }


    /**
     * <p>checkMethodHandle.</p>
     *
     * @param methodBody  a {@link io.github.hhy50.linker.generate.MethodBody} object.
     * @param lookupClass a {@link io.github.hhy50.linker.generate.bytecode.ClassTypeMember} object.
     * @param mhMember    a {@link io.github.hhy50.linker.generate.bytecode.MethodHandleMember} object.
     * @param objVar      a {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} object.
     */
    protected void checkMethodHandle(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar) {
        mhMember.ifNull(methodBody, body -> {
            mhReassign(body, lookupClass, mhMember, objVar);
        });
    }

    /**
     * <p>getClassLoadAction.</p>
     *
     * @param type a {@link org.objectweb.asm.Type} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     */
    protected Action getClassLoadAction(Type type) {
        return new DynamicClassLoad(type);
    }

    /**
     * <p>genericType.</p>
     *
     * @param methodType a {@link org.objectweb.asm.Type} object.
     * @return a {@link org.objectweb.asm.Type} object.
     */
    protected Type genericType(Type methodType) {
        Type rType = methodType.getReturnType();
        Type[] argsType = methodType.getArgumentTypes();
        if (!rType.equals(Type.VOID_TYPE) && AsmUtil.isObjectType(rType)) {
            rType = ObjectVar.TYPE;
        }
        for (int i = 0; i < argsType.length; i++) {
            if (!argsType[i].equals(Type.VOID_TYPE) && AsmUtil.isObjectType(argsType[i])) {
                argsType[i] = ObjectVar.TYPE;
            }
        }

        return Type.getMethodType(rType, argsType);
    }

    protected class DynamicClassLoad implements Action {
        private Type type;
        private LocalVarInst clazzVar;

        public DynamicClassLoad(Type type) {
            this.type = type;
        }

        @Override
        public void apply(MethodBody body) {
            if (AsmUtil.isPrimitiveType(type)) {
                LdcLoadAction.of(type).load(body);
                return;
            }
            if (this.clazzVar == null) {
                InvokeClassImplBuilder classImplBuilder = body.getClassBuilder();
                Action cl = new MethodInvokeAction(MethodHolder.GET_CLASS_LOADER)
                        .setInstance(
                                LdcLoadAction.of(AsmUtil.getType(classImplBuilder.getClassName()))
                        );
                this.clazzVar = body.newLocalVar(Type.getType(Class.class), new MethodInvokeAction(Runtime.GET_CLASS)
                        .setArgs(cl, LdcLoadAction.of(type.getClassName())));
            }
            clazzVar.load(body);
        }
    }
}
