package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodInvokeAction implements Action {

    private final MethodHolder methodHolder;
    private Action instance;
    private LoadAction[] args;

    public MethodInvokeAction(MethodHolder methodHolder) {
        this.methodHolder = methodHolder;
        this.args = new LoadAction[0];
    }

    @Override
    public void apply(MethodBody body) {
        MethodVisitor mv = body.getWriter();
        if (instance != null) {
            instance.apply(body);
        }
        for (LoadAction arg : args) {
            arg.apply(body);
        }
        mv.visitMethodInsn(instance != null ? Opcodes.INVOKEVIRTUAL : Opcodes.INVOKESTATIC,
                methodHolder.getOwner(), methodHolder.getMethodName(), methodHolder.getDesc(), false);
    }

    public MethodInvokeAction setInstance(Action instance) {
        this.instance = instance;
        return this;
    }

    public MethodInvokeAction setArgs(LoadAction... args) {
        this.args = args;
        return this;
    }
}
