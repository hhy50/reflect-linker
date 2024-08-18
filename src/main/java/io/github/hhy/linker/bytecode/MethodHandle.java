package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.LookupVar;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.runtime.Runtime;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy.linker.asm.AsmUtil.adaptLdcClassType;
import static org.objectweb.asm.Opcodes.*;

public abstract class MethodHandle {

    public void define(InvokeClassImplBuilder classImplBuilder) {

    }

    /**
     * 调用 mh.invoke();
     *
     * @return
     */
    public abstract ObjectVar invoke(MethodBody methodBody);


    /**
     * 初始化静态lookup
     * @param classImplBuilder
     * @param lookupMember
     * @param type
     */
    protected void initStaticLookup(InvokeClassImplBuilder classImplBuilder, LookupMember lookupMember, Type type) {
        if (lookupMember.isTargetLookup()) return;

        MethodBody clinit = classImplBuilder.getClinit();
        clinit.append(mv -> {
            mv.visitLdcInsn(type);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Runtime.OWNER, "lookup", Runtime.LOOKUP_DESC, false);
            lookupMember.store(clinit);
        });
    }

    /**
     * 初始化静态 methodhandle
     * @param classImplBuilder
     * @param mhMember
     * @param lookupMember
     * @param ownerType
     * @param fieldName
     * @param methodType
     * @param isStatic
     */
    protected void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember,
                                        Type ownerType, String fieldName, Type methodType, boolean isStatic) {

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
     * @param mhMember
     * @param objVar
     */
    protected void checkLookup(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        methodBody.append((mv) -> {
            mv.visitLabel(methodBody.getCheckLookupLabel());

            //  if (lookup == null || obj.getClass() != lookup.lookupClass())
            lookupMember.load(methodBody); // this.lookup
            mv.visitJumpInsn(IFNULL, methodBody.getLookupAssignLabel()); // this.lookup == null

            objVar.getClass(methodBody);  // obj.class
            lookupMember.lookupClass(methodBody);
            mv.visitJumpInsn(IF_ACMPEQ, methodBody.getCheckMhLabel()); // obj.class != this.lookup.lookupClass()

            mv.visitLabel(methodBody.getLookupAssignLabel());
            objVar.getClass(methodBody);  // obj.class
            mv.visitMethodInsn(INVOKESTATIC, Runtime.OWNER, "lookup", Runtime.LOOKUP_DESC, false); // Call Runtime.lookup()
            lookupMember.store(methodBody);

            mhReassign(methodBody, lookupMember, mhMember, objVar);
        });
    }

    /**
     * 通过一级的class获取字段, 主要是为了静态字段的访问
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
        methodBody.append((mv) -> {
            // if (obj == null)
            objVar.load(methodBody);
            mv.visitJumpInsn(IFNONNULL, methodBody.getCheckLookupLabel()); // obj == null

            lookupMember.load(methodBody);
            mv.visitJumpInsn(IFNONNULL, methodBody.getCheckLookupLabel()); // this.lookup != null

            prevLookupMember.lookupClass(methodBody); // prev_lookup.lookupClass()
            mv.visitLdcInsn(field.fieldName); // 'field'
            mv.visitMethodInsn(INVOKESTATIC, Runtime.OWNER, "findLookup", Runtime.FIND_LOOKUP_DESC, false); // Call Runtime.lookup()
            lookupMember.store(methodBody);
            mv.visitJumpInsn(Opcodes.GOTO, methodBody.getCheckMhLabel());
        });
    }

    protected void checkMethodHandle(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        methodBody.append(mv -> {
            Label endLabel = new Label();

            mv.visitLabel(methodBody.getCheckMhLabel());
            // if (mh == null)
            mhMember.load(methodBody); // this.mh
            mv.visitJumpInsn(IFNONNULL, endLabel);
            mhReassign(methodBody, lookupMember, mhMember, objVar);
            mv.visitLabel(endLabel);
        });
    }

    /**
     * mh 重新赋值字节码逻辑
     *
     * @param methodBody
     * @param lookupMember
     * @param mhMember
     * @param objVar
     */
    protected abstract void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar);
}
