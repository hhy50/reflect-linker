package io.github.hhy.linker.generate.setter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.TypeCastAction;
import io.github.hhy.linker.generate.bytecode.action.WrapTypeAction;
import io.github.hhy.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

public class SetterDecorator extends MethodHandle {

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
        typeCast(methodBody, methodBody.getArg(0), fieldRef.getType());
        setter.invoke(methodBody);
        AsmUtil.areturn(methodBody.getWriter(), Type.VOID_TYPE);
        return null;
    }

    private void typeCast(MethodBody methodBody, VarInst parameter, Type type) {
        // 校验入参类型
        if (!type.equals(parameter.getType())) {
            LocalVarInst varInst = null;
            if (AsmUtil.isPrimitiveType(parameter.getType())) {
                varInst = methodBody.newLocalVar(type, fieldRef.getUniqueName(), new WrapTypeAction(parameter, parameter.getType()));
            }
            varInst = methodBody.newLocalVar(type, fieldRef.getUniqueName(), new TypeCastAction(varInst == null ? parameter : varInst, type));
            methodBody.getArgs()[0] = varInst;
        }
    }

    @Override
    public void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        throw new RuntimeException("Decorator not impl mhReassign() method");
    }
}
