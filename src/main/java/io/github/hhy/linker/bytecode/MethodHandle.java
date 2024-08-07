package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

public abstract class MethodHandle {

    protected MethodHandle prevMethodHandle;

    public MethodHandle(MethodHandle prevMethodHandle) {
        this.prevMethodHandle = prevMethodHandle;
    }

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
     *      // mh = Runtime.findGetter(lookup, obj, "field"); // mhReassign()
     * }
     * </pre>
     *
     * @param methodBody
     * @param mhMember
     * @param objVar
     */
    protected void checkLookup(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        methodBody.append((mv) -> {
            if (lookupMember != null) {
                //  if (lookup == null || obj.getClass() != lookup.lookupClass())
                mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                mv.visitFieldInsn(Opcodes.GETFIELD, lookupMember.owner, lookupMember.memberName, lookupMember.type); // lookupMember.lookup
                mv.visitJumpInsn(Opcodes.IFNONNULL, new Label());

                mv.visitVarInsn(Opcodes.ALOAD, objVar.lvbIndex); // obj
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.GETFIELD, lookupMember.owner, lookupMember.memberName, lookupMember.type);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "lookupClass", "()Ljava/lang/Class;", false);

                mv.visitJumpInsn(Opcodes.IF_ICMPEQ, new Label());
                mv.visitVarInsn(Opcodes.ALOAD, objVar.lvbIndex); // a
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false); // a.getClass()
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Runtime", "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false); // Call Runtime.lookup()
                mv.visitFieldInsn(PUTFIELD, lookupMember.owner, lookupMember.memberName, "Ljava/lang/invoke/MethodHandles$Lookup;");
            } else {
                // if (mh == null)
                mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                mv.visitFieldInsn(Opcodes.GETFIELD, mhMember.owner, mhMember.memberName, mhMember.type); // this.mh
                mv.visitJumpInsn(Opcodes.IFNONNULL, new Label());
            }
            mhReassign(methodBody, lookupMember, mhMember, objVar);
        });
    }

    protected abstract void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar);
}
