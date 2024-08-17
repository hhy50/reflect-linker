package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHandle;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public class GetterWrapper extends MethodHandle {

    private Getter getter;
    private final Method methodDefine;

    public GetterWrapper(Getter getter, Method methodDefine) {
        this.getter = getter;
        this.methodDefine = methodDefine;
    }

    @Override
    public void define(InvokeClassImplBuilder classImplBuilder) {
        getter.define(classImplBuilder);
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        /**
         * get只需要对返回值进行转换就行
         */
        ObjectVar result = getter.invoke(methodBody);
        methodBody.append(mv -> {
            result.load(methodBody);
            Type reType = Type.getType(methodDefine.getReturnType());
            if (!result.type.equals(reType)) {
                mv.visitTypeInsn(Opcodes.CHECKCAST, reType.getInternalName());
            }
            AsmUtil.areturn(mv, reType);
        });
        return result;
    }

    @Override
    public void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        getter.mhReassign(methodBody, lookupMember, mhMember, objVar);
    }
}
