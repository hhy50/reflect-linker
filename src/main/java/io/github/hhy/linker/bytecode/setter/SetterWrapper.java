package io.github.hhy.linker.bytecode.setter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHandle;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.bytecode.vars.VarInst;
import io.github.hhy.linker.define.MethodDefine;
import org.objectweb.asm.Type;

import java.lang.reflect.Parameter;

import static org.objectweb.asm.Opcodes.CHECKCAST;

public class SetterWrapper extends MethodHandle {

    private Setter setter;
    private final MethodDefine methodDefine;

    public SetterWrapper(Setter setter, MethodDefine methodDefine) {
        this.setter = setter;
        this.methodDefine = methodDefine;
    }

    @Override
    public void define(InvokeClassImplBuilder classImplBuilder) {
        setter.define(classImplBuilder);
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        // 校验入参类型
        Parameter parameter = methodDefine.define.getParameters()[0];
        Type type = setter.field.getType();
        if (!type.equals(Type.getType(parameter.getType()))) {
            methodBody.append(mv -> {
                VarInst arg = methodBody.getArg(0);
                arg.load(methodBody);
                mv.visitTypeInsn(CHECKCAST, type.getInternalName());

                arg = new ObjectVar(methodBody.lvbIndex++, type);
                arg.store(methodBody);
                methodBody.args[0] = arg;
            });
        }
        setter.invoke(methodBody);
        AsmUtil.areturn(methodBody.writer, Type.VOID_TYPE);
        return null;
    }

    @Override
    public void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        setter.mhReassign(methodBody, lookupMember, mhMember, objVar);
    }
}
