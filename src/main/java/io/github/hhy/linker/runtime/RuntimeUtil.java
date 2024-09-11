package io.github.hhy.linker.runtime;


import io.github.hhy.linker.AccessTool;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.exceptions.LinkerException;
import io.github.hhy.linker.syslinker.DirectMethodHandleLinker;
import io.github.hhy.linker.syslinker.MemberNameLinker;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Modifier;

/**
 * <p>RuntimeUtil class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class RuntimeUtil {
    /** Constant <code>OWNER="io/github/hhy/linker/runtime/RuntimeUti"{trunked}</code> */
    public static final String OWNER = "io/github/hhy/linker/runtime/RuntimeUtil";
    /** Constant <code>IS_STATIC</code> */
    public static final MethodHolder IS_STATIC = new MethodHolder(OWNER, "isStatic", "(Ljava/lang/invoke/MethodHandle;)Z");
    /** Constant <code>UNWRAP_BYTE_DESC="(Ljava/lang/Object;)+Type.BYTE_TYPE"</code> */
    public static final String UNWRAP_BYTE_DESC = "(Ljava/lang/Object;)"+Type.BYTE_TYPE;
    /** Constant <code>UNWRAP_SHORT_DESC="(Ljava/lang/Object;)+Type.SHORT"</code> */
    public static final String UNWRAP_SHORT_DESC = "(Ljava/lang/Object;)"+Type.SHORT;
    /** Constant <code>UNWRAP_INT_DESC="(Ljava/lang/Object;)+Type.INT_TYPE"</code> */
    public static final String UNWRAP_INT_DESC = "(Ljava/lang/Object;)"+Type.INT_TYPE;
    /** Constant <code>UNWRAP_LONG_DESC="(Ljava/lang/Object;)+Type.LONG_TYPE"</code> */
    public static final String UNWRAP_LONG_DESC = "(Ljava/lang/Object;)"+Type.LONG_TYPE;
    /** Constant <code>UNWRAP_FLOAT_DESC="(Ljava/lang/Object;)+Type.FLOAT_TYPE"</code> */
    public static final String UNWRAP_FLOAT_DESC = "(Ljava/lang/Object;)"+Type.FLOAT_TYPE;
    /** Constant <code>UNWRAP_DOUBLE_DESC="(Ljava/lang/Object;)+Type.DOUBLE_TYPE"</code> */
    public static final String UNWRAP_DOUBLE_DESC = "(Ljava/lang/Object;)"+Type.DOUBLE_TYPE;
    /** Constant <code>UNWRAP_CHAR_DESC="(Ljava/lang/Object;)+Type.CHAR_TYPE"</code> */
    public static final String UNWRAP_CHAR_DESC = "(Ljava/lang/Object;)"+Type.CHAR_TYPE;
    /** Constant <code>UNWRAP_BOOL_DESC="(Ljava/lang/Object;)+Type.BOOLEAN_TYPE"</code> */
    public static final String UNWRAP_BOOL_DESC = "(Ljava/lang/Object;)"+Type.BOOLEAN_TYPE;
    /** Constant <code>TYPE_MATCH</code> */
    public static final MethodHolder TYPE_MATCH = new MethodHolder(OWNER, "typeMatch", "(Ljava/lang/Class;Ljava/lang/String;)Z");

    /**
     * <p>checkNull.</p>
     *
     * @param obj a {@link java.lang.Object} object.
     */
    public static void checkNull(Object obj) {

    }

    /**
     * <p>isStatic.</p>
     *
     * @param methodHandle a {@link java.lang.invoke.MethodHandle} object.
     * @return a boolean.
     * @throws io.github.hhy.linker.exceptions.LinkerException if any.
     */
    public static boolean isStatic(MethodHandle methodHandle) throws LinkerException {
        DirectMethodHandleLinker mh = AccessTool.createSysLinker(DirectMethodHandleLinker.class, methodHandle);
        MemberNameLinker member = mh.getMember();
        return Modifier.isStatic(member.modifiers());
    }

    /**
     * <p>typeMatch.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @param type a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean typeMatch(Class<?> clazz, String type) {
        if (clazz.isArray()) {
            return clazz.getCanonicalName().equals(type);
        }
        return ClassUtil.isAssignableFrom(clazz, type);
    }

    /**
     * <p>wrap.</p>
     *
     * @param i a byte.
     * @return a {@link java.lang.Object} object.
     */
    public static Object wrap(byte i) {
        return i;
    }

    /**
     * <p>wrap.</p>
     *
     * @param i a short.
     * @return a {@link java.lang.Object} object.
     */
    public static Object wrap(short i) {
        return i;
    }

    /**
     * <p>wrap.</p>
     *
     * @param i a int.
     * @return a {@link java.lang.Object} object.
     */
    public static Object wrap(int i) {
        return i;
    }

    /**
     * <p>wrap.</p>
     *
     * @param i a long.
     * @return a {@link java.lang.Object} object.
     */
    public static Object wrap(long i) {
        return i;
    }

    /**
     * <p>wrap.</p>
     *
     * @param i a float.
     * @return a {@link java.lang.Object} object.
     */
    public static Object wrap(float i) {
        return i;
    }

    /**
     * <p>wrap.</p>
     *
     * @param i a double.
     * @return a {@link java.lang.Object} object.
     */
    public static Object wrap(double i) {
        return i;
    }

    /**
     * <p>wrap.</p>
     *
     * @param i a char.
     * @return a {@link java.lang.Object} object.
     */
    public static Object wrap(char i) {
        return i;
    }

    /**
     * <p>wrap.</p>
     *
     * @param i a boolean.
     * @return a {@link java.lang.Object} object.
     */
    public static Object wrap(boolean i) {
        return i;
    }

    /**
     * <p>unwrapByte.</p>
     *
     * @param obj a {@link java.lang.Object} object.
     * @return a byte.
     */
    public static byte unwrapByte(Object obj) {
        if (obj instanceof Byte) {
            return (byte) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to byte", obj.getClass()));
    }

    /**
     * <p>unwrapShort.</p>
     *
     * @param obj a {@link java.lang.Object} object.
     * @return a short.
     */
    public static short unwrapShort(Object obj) {
        if (obj instanceof Short) {
            return (short) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to short", obj.getClass()));
    }

    /**
     * <p>unwrapInt.</p>
     *
     * @param obj a {@link java.lang.Object} object.
     * @return a int.
     */
    public static int unwrapInt(Object obj) {
        if (obj instanceof Integer) {
            return (int) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to int", obj.getClass()));
    }

    /**
     * <p>unwrapLong.</p>
     *
     * @param obj a {@link java.lang.Object} object.
     * @return a long.
     */
    public static long unwrapLong(Object obj) {
        if (obj instanceof Long) {
            return (long) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to long", obj.getClass()));
    }

    /**
     * <p>unwrapFloat.</p>
     *
     * @param obj a {@link java.lang.Object} object.
     * @return a float.
     */
    public static float unwrapFloat(Object obj) {
        if (obj instanceof Float) {
            return (float) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to float", obj.getClass()));
    }

    /**
     * <p>unwrapDouble.</p>
     *
     * @param obj a {@link java.lang.Object} object.
     * @return a double.
     */
    public static double unwrapDouble(Object obj) {
        if (obj instanceof Double) {
            return (double) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to double", obj.getClass()));
    }

    /**
     * <p>unwrapChar.</p>
     *
     * @param obj a {@link java.lang.Object} object.
     * @return a char.
     */
    public static char unwrapChar(Object obj) {
        if (obj instanceof Character) {
            return (char) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to char", obj.getClass()));
    }

    /**
     * <p>unwrapBool.</p>
     *
     * @param obj a {@link java.lang.Object} object.
     * @return a boolean.
     */
    public static boolean unwrapBool(Object obj) {
        if (obj instanceof Boolean) {
            return (boolean) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to boolean", obj.getClass()));
    }
}
