package io.github.hhy.linker.generate.setter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.generate.AbstractDecorator;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;


public class SetterDecorator extends AbstractDecorator {

    private Setter setter;
    private final FieldRef fieldRef;
    private final MethodDefine methodDefine;

    public SetterDecorator(Setter setter, FieldRef fieldRef, MethodDefine methodDefine) {
        this.setter = setter;
        this.fieldRef = fieldRef;
        this.methodDefine = methodDefine;
    }

    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        setter.define(classImplBuilder);
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        // 方法定义的类型
        typecastArgs(methodBody, methodBody.getArgs(), methodDefine.define.getParameterTypes(), new Type[]{fieldRef.getType()});
        setter.invoke(methodBody);
        AsmUtil.areturn(methodBody.getWriter(), Type.VOID_TYPE);
        return null;
    }

    @Override
    public void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        throw new RuntimeException("Decorator not impl mhReassign() method");
    }
}
