package io.github.hhy.linker.generate.bytecode.vars;

import org.objectweb.asm.Type;

public class LookupVar extends VarInst {

    public static final String OWNER = "java/lang/invoke/MethodHandles$Lookup";
    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandles$Lookup;";
    public static final String FIND_GETTER_DESC = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)"+MethodHandleVar.DESCRIPTOR;
    public static final Type TYPE = Type.getType(DESCRIPTOR);

    public LookupVar(int lvbIndex) {
        super(lvbIndex, TYPE);
    }
}
