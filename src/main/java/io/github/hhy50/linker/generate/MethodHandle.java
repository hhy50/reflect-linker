package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.LookupMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.action.RuntimeAction;
import io.github.hhy50.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Type;

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
     * @param classImplBuilder a {@link InvokeClassImplBuilder} object.
     */
    public final void define(InvokeClassImplBuilder classImplBuilder) {
        if (this.defined) return;
        define0(classImplBuilder);
        this.defined = true;
    }

    /**
     * <p>define0.</p>
     *
     * @param classImplBuilder a {@link InvokeClassImplBuilder} object.
     */
    protected void define0(InvokeClassImplBuilder classImplBuilder) {

    }

    /**
     * 调用 mh.invoke();
     *
     * @param methodBody a {@link MethodBody} object.
     * @return a {@link VarInst} object.
     */
    public abstract VarInst invoke(MethodBody methodBody);

    /**
     * 初始化静态 methodhandle
     *
     * @param clinit           a {@link MethodBody} object.
     * @param mhMember         a {@link MethodHandleMember} object.
     * @param lookupVar        a {@link VarInst} object.
     * @param ownerClassMember a {@link Type} object.
     * @param fieldName        a {@link String} object.
     * @param methodType       a {@link Type} object.
     * @param isStatic         a boolean.
     */
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, ClassTypeMember ownerClass, String fieldName, Type methodType, boolean isStatic) {

    }

    /**
     * <pre>
     * if (lookup == null || obj.getClass() != lookup.lookupClass()) {
     *      lookup = Runtime.lookup(obj.getClass());
     *      mh = Runtime.findGetter(lookup, lookup.lookupClass(), "field"); // mhReassign()
     * }
     * </pre>
     *
     * @param methodBody a {@link MethodBody} object.
     * @param lookupMember a {@link LookupMember} object.
     * @param mhMember a {@link MethodHandleMember} object.
     * @param varInst a {@link VarInst} object.
     */
    protected void checkLookup(MethodBody methodBody, VarInst lookupVar, MethodHandleMember mhMember, VarInst varInst) {
//        Action reinitLookup = (__) -> {
//            lookupMember.reinit(methodBody, varInst.getThisClass());
//            this.mhReassign(methodBody, lookupMember, mhMember, varInst);
//        };
//        lookupMember.runtimeCheck(methodBody, varInst, reinitLookup);
    }

    /**
     * 如果当前字段为null, 就以上级lookup获取字段的类型
     * <p>ps: 主要是为了静态字段的访问</p>
     * <pre>
     * if (obj == null) {
     *      lookup = Runtime.findLookup(prev_lookup.lookupClass(), 'field');
     * }
     * </pre>
     *
     * @param methodBody a {@link MethodBody} object.
     * @param lookupMember a {@link LookupMember} object.
     * @param field a {@link FieldRef} object.
     * @param prevLookupMember a {@link LookupMember} object.
     */
    protected void staticCheckLookup(MethodBody methodBody, LookupMember prevLookupMember, LookupMember lookupMember, FieldRef field) {
        lookupMember.ifNull(methodBody, (__) -> {
            lookupMember.store(methodBody, RuntimeAction.findLookup(prevLookupMember, field.fieldName));
        });
    }

    /**
     * <p>checkMethodHandle.</p>
     *
     * @param methodBody a {@link MethodBody} object.
     * @param lookupMember a {@link LookupMember} object.
     * @param mhMember a {@link MethodHandleMember} object.
     * @param objVar a {@link VarInst} object.
     */
    protected void checkMethodHandle(MethodBody methodBody, VarInst lookupVar, MethodHandleMember mhMember, VarInst objVar) {
        mhMember.ifNull(methodBody, body -> mhReassign(body, lookupVar, mhMember, objVar));
    }

    /**
     * <p>getClassLoadAction.</p>
     *
     * @param type a {@link org.objectweb.asm.Type} object.
     * @return a {@link Action} object.
     */
    protected Action getClassLoadAction(Type type) {
        return new DynamicClassLoad(type);
    }

    /**
     * mh 重新赋值字节码逻辑
     * <pre>
     *     mh = Runtime.findGetter(lookup, "c");
     * </pre>
     *
     * @param methodBody a {@link MethodBody} object.
     * @param lookupVar  a {@link LookupMember} object.
     * @param mhMember   a {@link MethodHandleMember} object.
     * @param objVar     a {@link VarInst} object.
     */
    protected abstract void mhReassign(MethodBody methodBody, VarInst lookupVar, MethodHandleMember mhMember, VarInst objVar);

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
