package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.Opcodes;

/**
 * 变量加载的操作
 */
public interface LoadAction extends Action {

    Action LOAD0 = new LoadAction() {
        @Override
        public void load(MethodBody body) {
            body.getWriter().visitVarInsn(Opcodes.ALOAD, 0);
        }
    };

    @Override
    default void apply(MethodBody body) {
        load(body);
    }

    void load(MethodBody body);
}
