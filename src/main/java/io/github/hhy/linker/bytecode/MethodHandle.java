package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import org.objectweb.asm.Label;

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
     * <pre>
     * if (lookup == null || obj.getClass() != lookup.lookupClass()) {
     *      lookup = Runtime.lookup(obj.getClass());
     *      mh = Runtime.findGetter(lookup, obj, "field"); // mhReassign()
     * }
     * </pre>
     *
     * @param methodBody
     * @param mhMember
     * @param objVar
     */
    protected void checkLookup(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        methodBody.append((mv) -> {
            Label endLabel = new Label();
            Label initLabel = new Label();

            //  if (lookup == null || obj.getClass() != lookup.lookupClass())
            lookupMember.load(methodBody); // this.lookup
            mv.visitJumpInsn(IFNULL, initLabel); // this.lookup == null

            objVar.thisClass(methodBody);  // obj.class
            lookupMember.load(methodBody); // this.lookup
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "lookupClass", "()Ljava/lang/Class;", false);
            mv.visitJumpInsn(IF_ACMPEQ, endLabel); // obj.class != this.lookup.lookupClass()

            mv.visitLabel(initLabel);
            objVar.thisClass(methodBody);  // obj.class
            mv.visitMethodInsn(INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false); // Call Runtime.lookup()
            lookupMember.store(methodBody);

            mhReassign(methodBody, lookupMember, mhMember, objVar);
            mv.visitLabel(endLabel);
        });
    }

    protected void checkMethodHandle(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        methodBody.append(mv -> {
            Label endLabel = new Label();
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
