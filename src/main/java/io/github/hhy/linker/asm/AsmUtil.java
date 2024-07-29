package io.github.hhy.linker.asm;

import io.github.hhy.linker.util.ClassUtil;

public class AsmUtil {

    public static String toTypeDesc(String className) {
        return "L" + ClassUtil.className2path(className) + ";";
    }

    public static AsmClassBuilder defineClass(int access, String className, String superName, String[] interfaces, String sign) {
        return new AsmClassBuilder(access, className, superName, interfaces, sign);
    }
}
