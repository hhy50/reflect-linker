package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.AbstractDecorator;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;

import java.lang.reflect.Method;

/**
 * The type Getter decorator.
 */
public class GetterDecorator extends AbstractDecorator {

    private Getter<?> getter;
    private final FieldRef fieldRef;
    private final Method methodDefine;

    /**
     * Instantiates a new Getter decorator.
     *
     * @param getter       the getter
     * @param fieldRef     the field ref
     * @param methodDefine the method define
     */
    public GetterDecorator(Getter getter, FieldRef fieldRef, Method methodDefine) {
        this.getter = getter;
        this.fieldRef = fieldRef;
        this.methodDefine = methodDefine;
    }

    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        getter.define(classImplBuilder);
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        /**
         * get只需要对返回值进行转换就行
         */
        VarInst result = getter.invoke(methodBody);
        typecastResult(methodBody, result, methodDefine.getReturnType())
                .returnThis();
        return null;
    }
}
