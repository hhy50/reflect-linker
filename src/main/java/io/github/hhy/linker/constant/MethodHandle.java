package io.github.hhy.linker.constant;


import org.objectweb.asm.Type;

/**
 * <p>MethodHandle class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class MethodHandle {
    /** Constant <code>DESCRIPTOR="Ljava/lang/invoke/MethodHandle;"</code> */
    public static final String DESCRIPTOR = "Ljava/lang/invoke/MethodHandle;";
    /** Constant <code>TYPE</code> */
    public static final Type TYPE = Type.getType(DESCRIPTOR);
}
