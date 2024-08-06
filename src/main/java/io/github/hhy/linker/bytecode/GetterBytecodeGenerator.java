package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.define.RuntimeField;
import io.github.hhy.linker.define.TargetField;
import io.github.hhy.linker.runtime.Runtime;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static io.github.hhy.linker.asm.AsmUtil.adaptLdcClassType;

public class GetterBytecodeGenerator implements BytecodeGenerator {
    private final TargetField target;


    public GetterBytecodeGenerator(TargetField target) {
        this.target = target;
    }

    /**
     * 生成
     *
     * @param writer
     */
    public void generate(InvokeClassImplBuilder classBuilder, MethodVisitor writer) {
        // 需要先生成 lookup 和 MethodHandle
        createGetterMhInvoker(classBuilder, target);
    }


    /**
     * @param classBuilder
     * @param target
     * @return
     */
    private GetterMethodHandleHolder createGetterMhInvoker(InvokeClassImplBuilder classBuilder, TargetField target) {
        GetterMethodHandleHolder prev = null;
        if (target.getPrev() != null) {
            prev = createGetterMhInvoker(classBuilder, target.getPrev());
        }
        if (prev == null && target instanceof RuntimeField) {
            prev = GetterMethodHandleHolder.target(classBuilder.bindTarget);
        }
        GetterMethodHandleHolder setterMh = new GetterMethodHandleHolder(prev, target);


        return setterMh;
    }
}
