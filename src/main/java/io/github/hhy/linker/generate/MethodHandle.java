package io.github.hhy.linker.generate;

import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.Action;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

public abstract class MethodHandle {

    protected boolean defined = false;

    public final void define(InvokeClassImplBuilder classImplBuilder) {
        if (this.defined) return;
        define0(classImplBuilder);
        this.defined = true;
    }

    protected void define0(InvokeClassImplBuilder classImplBuilder) {

    }

    /**
     * 调用 mh.invoke();
     *
     * @return
     */
    public abstract VarInst invoke(MethodBody methodBody);

    /**
     * 初始化静态 methodhandle
     *
     * @param classImplBuilder
     * @param mhMember
     * @param lookupMember
     * @param ownerType
     * @param fieldName
     * @param methodType
     * @param isStatic
     */
    protected void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember, Type ownerType, String fieldName, Type methodType, boolean isStatic) {

    }

    /**
     * <pre>
     * if (lookup == null || obj.getClass() != lookup.lookupClass()) {
     *      lookup = Runtime.lookup(obj.getClass());
     *      mh = Runtime.findGetter(lookup, lookup.lookupClass(), "field"); // mhReassign()
     * }
     * </pre>
     *
     * @param methodBody
     * @param lookupMember
     * @param mhMember
     * @param varInst
     */
    protected void checkLookup(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst varInst) {
        Action reinitLookup = (__) -> {
            lookupMember.reinit(methodBody, varInst);
            this.mhReassign(methodBody, lookupMember, mhMember, varInst);
        };
        lookupMember.runtimeCheck(methodBody, varInst, reinitLookup);
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
     * @param methodBody
     * @param lookupMember
     * @param objVar
     * @param field
     */
    protected void staticCheckLookup(MethodBody methodBody, LookupMember prevLookupMember, LookupMember lookupMember, ObjectVar objVar, FieldRef field) {
//        methodBody.append((mv) -> {

            // if (obj == null)
//            objVar.ifNull(methodBody, );

            //objVar.load(methodBody);
            //mv.visitJumpInsn(IFNONNULL, methodBody.getCheckLookupLabel()); // obj == null

//            lookupMember.load(methodBody);
//            mv.visitJumpInsn(IFNONNULL, methodBody.getCheckLookupLabel()); // this.lookup != null
//
//            prevLookupMember.lookupClass(methodBody); // prev_lookup.lookupClass()
//            mv.visitLdcInsn(field.fieldName); // 'field'
//            mv.visitMethodInsn(INVOKESTATIC, Runtime.OWNER, "findLookup", Runtime.FIND_LOOKUP_DESC, false); // Call Runtime.lookup()
//            lookupMember.store(methodBody);
//            mv.visitJumpInsn(Opcodes.GOTO, getCheckMhLabel());
//        });
    }

    protected void checkMethodHandle(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        mhMember.ifNull(methodBody, body -> mhReassign(body, lookupMember, mhMember, objVar));
    }

    /**
     * mh 重新赋值字节码逻辑
     * <pre>
     *     mh = Runtime.findGetter(lookup, "c");
     * </pre>
     *
     * @param methodBody
     * @param lookupMember
     * @param mhMember
     * @param objVar
     */
    protected abstract void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar);
}
