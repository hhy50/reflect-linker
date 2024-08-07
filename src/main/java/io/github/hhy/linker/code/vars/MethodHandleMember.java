package io.github.hhy.linker.code.vars;


import io.github.hhy.linker.code.MethodBody;
import io.github.hhy.linker.define.Field;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

public class MethodHandleMember extends Member {


    private final Field field;

    public MethodHandleMember(String owner, String mhVarName, Field field) {
        super(owner, mhVarName, MethodHandleVar.DESCRIPTOR);
        this.field = field;
    }


    /**
     * <pre>
     *     if (mh == null) {
     *         mh = Runtime.findGetter(lookup, obj, "c");
     *     }
     * </pre>
     *
     * @param methodBody
     */
    public void checkLookup(MethodBody methodBody, LookupMember lookupMember, ObjectVar objVar) {
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitFieldInsn(Opcodes.GETFIELD, this.owner, this.memberName, this.type); // this.mh
            mv.visitJumpInsn(Opcodes.IFNONNULL, new Label());

            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitFieldInsn(GETFIELD, lookupMember.owner, lookupMember.memberName, lookupMember.type); // this.lookup
            mv.visitVarInsn(ALOAD, objVar.lvbIndex); // this.obj
            mv.visitLdcInsn(field.getFieldName()); // 'c'
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Runtime", "findGetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;", false); // Call Runtime.findGetter()
            mv.visitFieldInsn(PUTFIELD, this.owner, this.memberName, this.type);
        });

    }
}
