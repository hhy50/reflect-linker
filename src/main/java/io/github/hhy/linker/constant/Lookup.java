package io.github.hhy.linker.constant;

import org.objectweb.asm.Type;

/**
 * <p>Lookup class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class Lookup {
    /** Constant <code>OWNER="java/lang/invoke/MethodHandles$Lookup"</code> */
    public static final String OWNER = "java/lang/invoke/MethodHandles$Lookup";
    /** Constant <code>DESCRIPTOR="Ljava/lang/invoke/MethodHandles$Lookup;"</code> */
    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandles$Lookup;";
    /** Constant <code>TYPE</code> */
    public static final Type TYPE = Type.getType(DESCRIPTOR);
    /** Constant <code>FIND_XETTER_DESC="(Ljava/lang/Class;Ljava/lang/String;Lja"{trunked}</code> */
    public static final String FIND_XETTER_DESC = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)"+MethodHandle.DESCRIPTOR;
    /** Constant <code>FIND_VIRTUAL="(Ljava/lang/Class;Ljava/lang/String;Lja"{trunked}</code> */
    public static final String FIND_VIRTUAL = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)"+MethodHandle.DESCRIPTOR;
    /** Constant <code>FIND_SPECIAL="(Ljava/lang/Class;Ljava/lang/String;Lja"{trunked}</code> */
    public static final String FIND_SPECIAL = "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)"+MethodHandle.DESCRIPTOR;
}
