package io.github.hhy.linker.generator;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.define.InvokeClassDefine;
import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.MethodDefine;
import org.objectweb.asm.Opcodes;


public class BytecodeGenerator {

    public <T> T generate(Class<T> define, InvokeClassDefine methodDefines) throws ClassNotFoundException {
        AsmClassBuilder classBuilder
                = AsmUtil.defineClass(Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN, define.getName() + "$$default", null, new String[]{define.getName()}, "");
        for (MethodDefine methodDefine : methodDefines.getMethodDefines()) {
            classBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodDefine.getMethodName(), methodDefine.getMethodDesc(), null, null);
        }
        classBuilder.end();
        String className = classBuilder.getClassName();

        return null;
    }
}
