package io.github.hhy.linker.bytecode.vars;


import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.runtime.Runtime;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class LookupMember extends Member {

    private boolean isTargetLookup;

    /**
     * 防止多次静态初始化
     */
    private boolean inited;

    public LookupMember(int access, String owner, String lookupName) {
        super(access, owner, lookupName, LookupVar.TYPE);
    }

    public void lookupClass(MethodBody methodBody) {
        methodBody.append(mv -> {
            load(methodBody);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, LookupVar.OWNER, "lookupClass", "()Ljava/lang/Class;", false);
        });
    }

    /**
     * 初始化静态lookup, 生成以下代码
     * <pre>
     * static {
     *     lookup = Runtime.lookup(Type.class);
     * }
     * </pre>
     *
     * @param clinit
     * @param type
     */
    public void staticInit(MethodBody clinit, Type type) {
        if (inited) return;
        clinit.append(mv -> {
            mv.visitLdcInsn(type);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Runtime.OWNER, "lookup", Runtime.LOOKUP_DESC, false);
            store(clinit);
        });
        this.inited = true;
    }

    public void reinit(MethodBody method, ObjectVar objectVar) {
        method.append(mv -> {
            objectVar.getClass(method);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Runtime.OWNER, "lookup", Runtime.LOOKUP_DESC, false);
            store(method);
        });
    }

    public boolean isTargetLookup() {
        return this.isTargetLookup;
    }

    public void isTarget(boolean b) {
        this.isTargetLookup = b;
    }
}
