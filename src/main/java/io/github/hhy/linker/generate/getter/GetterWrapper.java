package io.github.hhy.linker.generate.getter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.TypeCastAction;
import io.github.hhy.linker.generate.bytecode.action.UnwrapTypeAction;
import io.github.hhy.linker.generate.bytecode.action.WrapTypeAction;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public class GetterWrapper extends MethodHandle {

    private Getter getter;
    private final FieldRef fieldRef;
    private final Method methodDefine;

    public GetterWrapper(Getter getter, FieldRef fieldRef, Method methodDefine) {
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
        Type rType = typecast(methodBody, result, Type.getType(methodDefine.getReturnType()));
        AsmUtil.areturn(methodBody.getWriter(), rType);
        return null;
    }

    /**
     * 对于基本数据类型进行拆箱
     *
     * @param methodBody
     * @param result
     * @param rType
     */
    private Type typecast(MethodBody methodBody, VarInst result, Type rType) {
        if (!result.getType().equals(rType)) {
            if (AsmUtil.isPrimitiveType(rType)) {
                // 拆箱
                methodBody.append(() -> new UnwrapTypeAction(result, rType));
            } else if (AsmUtil.isPrimitiveType(result.getType())) {
                methodBody.append(() -> new WrapTypeAction(result, result.getType()));
            } else {
                methodBody.append(() -> new TypeCastAction(result, rType));
            }
        } else {
            result.load(methodBody);
        }
        return rType;
    }

    @Override
    public void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        getter.mhReassign(methodBody, lookupMember, mhMember, objVar);
    }
}
