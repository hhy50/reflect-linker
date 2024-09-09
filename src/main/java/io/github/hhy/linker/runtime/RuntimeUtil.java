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

public class RuntimeUtil {
    public static final String OWNER = "io/github/hhy/linker/runtime/RuntimeUtil";
    public static final MethodHolder IS_STATIC = new MethodHolder(OWNER, "isStatic", "(Ljava/lang/invoke/MethodHandle;)Z");
    public static final String UNWRAP_BYTE_DESC = "(Ljava/lang/Object;)"+Type.BYTE_TYPE;
    public static final String UNWRAP_SHORT_DESC = "(Ljava/lang/Object;)"+Type.SHORT;
    public static final String UNWRAP_INT_DESC = "(Ljava/lang/Object;)"+Type.INT_TYPE;
    public static final String UNWRAP_LONG_DESC = "(Ljava/lang/Object;)"+Type.LONG_TYPE;
    public static final String UNWRAP_FLOAT_DESC = "(Ljava/lang/Object;)"+Type.FLOAT_TYPE;
    public static final String UNWRAP_DOUBLE_DESC = "(Ljava/lang/Object;)"+Type.DOUBLE_TYPE;
    public static final String UNWRAP_CHAR_DESC = "(Ljava/lang/Object;)"+Type.CHAR_TYPE;
    public static final String UNWRAP_BOOL_DESC = "(Ljava/lang/Object;)"+Type.BOOLEAN_TYPE;
    public static final MethodHolder TYPE_MATCH = new MethodHolder(OWNER, "typeMatch", "(Ljava/lang/Class;Ljava/lang/String;)Z");

    public static void checkNull(Object obj) {

    }

    public static boolean isStatic(MethodHandle methodHandle) throws LinkerException {
        DirectMethodHandleLinker mh = AccessTool.createSysLinker(DirectMethodHandleLinker.class, methodHandle);
        MemberNameLinker member = mh.getMember();
        return Modifier.isStatic(member.modifiers());
    }

    public static boolean typeMatch(Class<?> clazz, String type) {
        if (clazz.isArray()) {
            return clazz.getCanonicalName().equals(type);
        }
        return ClassUtil.isAssignableFrom(clazz, type);
    }

    public static Object wrap(byte i) {
        return i;
    }

    public static Object wrap(short i) {
        return i;
    }

    public static Object wrap(int i) {
        return i;
    }

    public static Object wrap(long i) {
        return i;
    }

    public static Object wrap(float i) {
        return i;
    }

    public static Object wrap(double i) {
        return i;
    }

    public static Object wrap(char i) {
        return i;
    }

    public static Object wrap(boolean i) {
        return i;
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
