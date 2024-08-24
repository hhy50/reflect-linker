package io.github.hhy.linker.generate.setter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.vars.LookupMember;
import io.github.hhy.linker.generate.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

import java.lang.reflect.Parameter;

import static org.objectweb.asm.Opcodes.CHECKCAST;

public class SetterWrapper extends MethodHandle {

    private Setter setter;
    private final FieldRef fieldRef;
    private final MethodDefine methodDefine;

    public SetterWrapper(Setter setter, FieldRef fieldRef, MethodDefine methodDefine) {
        this.setter = setter;
        this.fieldRef = fieldRef;
        this.methodDefine = methodDefine;
    }

    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        setter.define(classImplBuilder);
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        // 校验入参类型
        // 方法定义的类型
        Parameter parameter = methodDefine.define.getParameters()[0];
        // 字段实际类型
        Type type = fieldRef.getType();
        if (!type.equals(Type.getType(parameter.getType()))) {
            methodBody.append(mv -> {
                VarInst arg = methodBody.getArg(0);
                arg.load(methodBody);
                mv.visitTypeInsn(CHECKCAST, type.getInternalName());

                arg = new ObjectVar(methodBody.lvbIndex++, type);
                arg.store(methodBody);
                methodBody.setArg(0,arg);
            });
        }
        setter.invoke(methodBody);
        methodBody.append(mv -> {
            AsmUtil.areturn(mv, Type.VOID_TYPE);
        });
        return null;
    }

    @Override
    public void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        setter.mhReassign(methodBody, lookupMember, mhMember, objVar);
    }
}
