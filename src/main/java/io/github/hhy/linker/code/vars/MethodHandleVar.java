package io.github.hhy.linker.code.vars;



public class MethodHandleVar extends VarInst {

    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandle;";

    public MethodHandleVar(int index) {
        super(index, DESCRIPTOR);
    }


}
