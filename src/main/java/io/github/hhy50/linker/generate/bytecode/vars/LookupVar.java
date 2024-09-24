package io.github.hhy50.linker.generate.bytecode.vars;

import io.github.hhy50.linker.constant.MethodHandle;
import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Type;


/**
 * <p>LookupVar class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class LookupVar extends VarInst {
    /** Constant <code>OWNER="java/lang/invoke/MethodHandles$Lookup"</code> */
    public static final String OWNER = "java/lang/invoke/MethodHandles$Lookup";
    /** Constant <code>DESCRIPTOR="Ljava/lang/invoke/MethodHandles$Lookup;"</code> */
    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandles$Lookup;";

    /** Constant <code>TYPE</code> */
    public static final Type TYPE = Type.getType(DESCRIPTOR);
    /** Constant <code>FIND_XETTER_DESC="(Ljava/lang/Class;Ljava/lang/String;Lja"{trunked}</code> */
    public static final String FIND_XETTER_DESC = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)"+ MethodHandle.DESCRIPTOR;
    /** Constant <code>FIND_VIRTUAL="(Ljava/lang/Class;Ljava/lang/String;Lja"{trunked}</code> */
    public static final String FIND_VIRTUAL = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)"+MethodHandle.DESCRIPTOR;
    /** Constant <code>FIND_SPECIAL="(Ljava/lang/Class;Ljava/lang/String;Lja"{trunked}</code> */
    public static final String FIND_SPECIAL = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)"+MethodHandle.DESCRIPTOR;

    /**
     * <p>Constructor for VarInst.</p>
     *
     * @param lvbIndex a int.
     * @param type     a {@link org.objectweb.asm.Type} object.
     */
    public LookupVar(MethodBody body, int lvbIndex, Type type) {
        super(body, lvbIndex, type);
    }
}
