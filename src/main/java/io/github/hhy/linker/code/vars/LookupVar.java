package io.github.hhy.linker.code.vars;

public class LookupVar extends VarInst {

    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandles/Lookup;";

    public LookupVar(int lvbIndex, String typeDesc) {
        super(lvbIndex, DESCRIPTOR);
    }
}
