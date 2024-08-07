package io.github.hhy.linker.code.vars;


import io.github.hhy.linker.code.MethodBody;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;


public class LookupMember extends Member {


    public LookupMember(String owner, String lookupName) {
        super(owner, lookupName, LookupVar.DESCRIPTOR);
    }

    /**
     * <pre>
     * if (a_lookup == null || a.getClass() != a_lookup.lookupClass()) {
     *      a_lookup = Runtime.lookup(a.getClass());
     *      a_c_getter_mh = Runtime.findGetter(a_lookup, a, "c");
     * }
     * </pre>
     *
     * @param methodBody
     * @param objVar
     */
    public void checkLookup(MethodBody methodBody, ObjectVar objVar) {
        methodBody.append((mv) -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitFieldInsn(Opcodes.GETFIELD, this.owner, this.memberName, this.type); // this.lookup
            mv.visitJumpInsn(Opcodes.IFNONNULL, new Label());

            mv.visitVarInsn(Opcodes.ALOAD, objVar.lvbIndex); // a
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, this.owner, this.memberName, this.type);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "lookupClass", "()Ljava/lang/Class;", false);

            mv.visitJumpInsn(Opcodes.IF_ICMPEQ, new Label());
            mv.visitVarInsn(Opcodes.ALOAD, objVar.lvbIndex); // a
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false); // a.getClass()
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Runtime", "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false); // Call Runtime.lookup()
            mv.visitFieldInsn(PUTFIELD, this.owner, this.memberName, "Ljava/lang/invoke/MethodHandles$Lookup;");
        });
    }
}
