package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.define.RuntimeField;
import io.github.hhy.linker.define.Field;
import org.objectweb.asm.MethodVisitor;

public class GetterBytecodeGenerator implements BytecodeGenerator {
    private final Field target;


    public GetterBytecodeGenerator(Field target) {
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
    private GetterMethodHandleInvoker createGetterMhInvoker(InvokeClassImplBuilder classBuilder, Field target) {
        GetterMethodHandleInvoker prev = null;
        if (target.getPrev() != null) {
            prev = createGetterMhInvoker(classBuilder, target.getPrev());
        }
        if (prev == null && target instanceof RuntimeField) {
            prev = GetterMethodHandleInvoker.target(classBuilder.bindTarget);
        }
        GetterMethodHandleInvoker setterMh = new GetterMethodHandleInvoker(prev, target);
        setterMh.define(classBuilder);
        return setterMh;
    }
}
