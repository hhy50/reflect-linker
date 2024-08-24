package io.github.hhy.linker.generate.bytecode.vars;


import org.objectweb.asm.Type;

public class MethodHandleVar extends VarInst {

    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandle;";

    public static final Type TYPE = Type.getType(DESCRIPTOR);

    public MethodHandleVar(int index) {
        super(index, TYPE);
    }
}
