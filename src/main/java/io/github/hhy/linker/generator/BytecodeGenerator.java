package io.github.hhy.linker.generator;

import io.github.hhy.linker.define.InvokeClassDefine;
import io.github.hhy.linker.asm.AsmUtil;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;

public class BytecodeGenerator {

    public <T> T generate(Class<T> define, InvokeClassDefine methodDefines) throws ClassNotFoundException {
        AsmUtil.defineClass(Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN, define.getName() + "$$default", null, new String[]{define.getName()}, "");
        return null;
    }
}
