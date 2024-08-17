package io.github.hhy.linker.bytecode.vars;


import io.github.hhy.linker.bytecode.MethodBody;
import org.objectweb.asm.Opcodes;

public class LookupMember extends Member {

    private boolean isTargetLookup;

    public LookupMember(int access, String owner, String lookupName) {
        super(access, owner, lookupName, LookupVar.TYPE);
    }

    public LookupMember(String owner, String lookupName) {
        super(owner, lookupName, LookupVar.TYPE);
    }

    public void lookupClass(MethodBody methodBody) {
        methodBody.append(mv -> {
            load(methodBody);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, LookupVar.OWNER, "lookupClass", "()Ljava/lang/Class;", false);
        });
    }

    public boolean isTargetLookup() {
        return this.isTargetLookup;
    }

    public void isTarget(boolean b) {
        this.isTargetLookup = b;
    }
}
