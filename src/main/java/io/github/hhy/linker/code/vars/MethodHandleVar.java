package io.github.hhy.linker.code.vars;


import org.objectweb.asm.MethodVisitor;

public class MethodHandleVar extends VarInst {

    public MethodHandleVar(int index) {
        super(index, "Ljava/lang/invoke/MethodHandle;");
    }


}
