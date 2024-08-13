package io.github.hhy.linker.bytecode.vars;


import io.github.hhy.linker.bytecode.MethodBody;
import org.objectweb.asm.Opcodes;

public class LookupMember extends Member {

    public LookupMember(int access, String owner, String lookupName) {
        super(access, owner, lookupName, LookupVar.DESCRIPTOR);
    }

    public LookupMember(String owner, String lookupName) {
        super(owner, lookupName, LookupVar.DESCRIPTOR);
    }

    public void lookupClass(MethodBody methodBody) {
        methodBody.append(mv -> {
            load(methodBody);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "lookupClass", "()Ljava/lang/Class;", false);
        });
    }
}
