package io.github.hhy.linker.generate.bytecode;


import io.github.hhy.linker.constant.Lookup;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.action.Action;
import io.github.hhy.linker.generate.bytecode.action.JumpAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.runtime.Runtime;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy.linker.generate.bytecode.vars.VarInst.OBJECT_GET_CLASS;

public class LookupMember extends Member {

    public static final MethodHolder LOOKUP_LOOKUP_CLASS = new MethodHolder(Lookup.OWNER, "lookupClass", "()Ljava/lang/Class;");

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
     *
     * @param access
     * @param owner
     * @param lookupName
     * @param staticType
     */
    public LookupMember(int access, String owner, String lookupName, Type staticType) {
        super(access, owner, lookupName, Lookup.TYPE);
        this.staticType = staticType;
    }

    /**
     * 没有静态类型的构造， 只能在运行时初始化
     *
     * @param access
     * @param owner
     * @param lookupName
     */
    public LookupMember(int access, String owner, String lookupName) {
        super(access, owner, lookupName, Lookup.TYPE);
        this.staticType = null;
    }

    public void lookupClass(MethodBody methodBody) {
        methodBody.append(mv -> {
            load(methodBody);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Lookup.OWNER, "lookupClass", "()Ljava/lang/Class;", false);
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

    public void reinit(MethodBody methodBody, VarInst objectVar) {
        this.store(methodBody, new MethodInvokeAction(Runtime.LOOKUP)
                .setArgs(new MethodInvokeAction(OBJECT_GET_CLASS)
                        .setInstance(objectVar))
        );
    }

    /**
     * 生成运行时校验lookup的代码
     * <pre>
     *     if (obj.getClass() != lookup.lookupClass()) {
     *         // goto jump
     *     }
     * </pre>
     *
     * @param methodBody
     * @param varInst
     * @param jump
     */
    public void runtimeCheck(MethodBody methodBody, VarInst varInst, JumpAction lookupAssign, JumpAction mhCheck) {
        methodBody.append(() -> {
            MethodInvokeAction getClass = new MethodInvokeAction(OBJECT_GET_CLASS).setInstance(varInst);
            MethodInvokeAction lookupClass = new MethodInvokeAction(LOOKUP_LOOKUP_CLASS).setInstance(this);
            return Action.ifNotEq(getClass, lookupClass, lookupAssign, mhCheck);
        });
    }

    public boolean isTargetLookup() {
        return this.isTargetLookup;
    }

    public void isTarget(boolean b) {
        this.isTargetLookup = b;
    }
}
