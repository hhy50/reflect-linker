package io.github.hhy.linker.generate.getter;

import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.generate.AbstractDecorator;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.util.AnnotationUtils;
import io.github.hhy.linker.util.StringUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public class GetterDecorator extends AbstractDecorator {

    private Getter<?> getter;
    private final FieldRef fieldRef;
    private final Method methodDefine;

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
        result = typecastResult(methodBody, result, methodDefine.getReturnType());
        result.returnThis(methodBody);
        return null;
    }

    private VarInst wrapLinker(MethodBody methodBody, VarInst result, Class<?> linkerClass) {
        return null;
    }

    @Override
    public void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        throw new RuntimeException("Decorator not impl mhReassign() method");
    }
}
