package io.github.hhy.linker.bytecode.vars;


import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.runtime.Runtime;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class LookupMember extends Member {

    private boolean isTargetLookup;

    /**
     * lookupClass
     */
    private Type staticType;

    /**
     * 防止多次静态初始化
     */
    private boolean inited;

    /**
     * 拥有静态类型的构造
     * @param access
     * @param owner
     * @param lookupName
     * @param staticType
     */
    public LookupMember(int access, String owner, String lookupName, Type staticType) {
        super(access, owner, lookupName, LookupVar.TYPE);
        this.staticType = staticType;
    }

    /**
     * 没有静态类型的构造， 只能在运行时初始化
     * @param access
     * @param owner
     * @param lookupName
     */
    public LookupMember(int access, String owner, String lookupName) {
        super(access, owner, lookupName, LookupVar.TYPE);
        this.staticType = null;
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
    public void staticInit(MethodBody clinit) {
        if (inited) return;
        clinit.append(mv -> {
            mv.visitLdcInsn(staticType);
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
