package io.github.hhy.linker.runtime;


import org.objectweb.asm.Type;

public class RuntimeUtil {
    public static final String RUNTIME_UTIL_OWNER = "io/github/hhy/linker/runtime/RuntimeUtil";
    public static final String UNWRAP_BYTE_DESC = "(Ljava/lang/Object;)"+Type.BYTE_TYPE;
    public static final String UNWRAP_SHORT_DESC = "(Ljava/lang/Object;)"+Type.SHORT;
    public static final String UNWRAP_INT_DESC = "(Ljava/lang/Object;)"+Type.INT_TYPE;
    public static final String UNWRAP_LONG_DESC = "(Ljava/lang/Object;)"+Type.LONG_TYPE;

    public static void checkNull(Object obj) {


    }

    public static byte unwrapByte(Object obj) {
        if (obj instanceof Byte) {
            return (byte) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to byte", obj.getClass()));
    }

    public static short unwrapShort(Object obj) {
        if (obj instanceof Short) {
            return (short) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to short", obj.getClass()));
    }

    public static int unwrapInt(Object obj) {
        if (obj instanceof Integer) {
            return (int) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to int", obj.getClass()));
    }

    public static long unwrapLong(Object obj) {
        if (obj instanceof Long) {
            return (long) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to long", obj.getClass()));
    }

    public static float unwrapFloat(Object obj) {
        if (obj instanceof Float) {
            return (float) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to float", obj.getClass()));
    }

    public static double unwrapDouble(Object obj) {
        if (obj instanceof Double) {
            return (double) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to double", obj.getClass()));
    }

    public static char unwrapChar(Object obj) {
        if (obj instanceof Character) {
            return (char) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to char", obj.getClass()));
    }

    public static boolean unwrapBool(Object obj) {
        if (obj instanceof Boolean) {
            return (boolean) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to boolean", obj.getClass()));

    }
}
