package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHandle;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.runtime.RuntimeUtil;
import org.objectweb.asm.Opcodes;
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
    public ObjectVar invoke(MethodBody methodBody) {
        /**
         * get只需要对返回值进行转换就行
         */
        methodBody.append(mv -> {
            ObjectVar result = getter.invoke(methodBody);
            result.checkNullPointer(methodBody, fieldRef.getFullName());

            Type rType = Type.getType(methodDefine.getReturnType());
            unwrap(methodBody, result, rType);
            AsmUtil.areturn(mv, rType);
        });
        return null;
    }

    /**
     * 对于基本数据类型进行拆箱
     *
     * @param methodBody
     * @param result
     * @param rType
     */
    private void unwrap(MethodBody methodBody, ObjectVar result, Type rType) {
        if (!result.getType().equals(rType)) {
            methodBody.append(mv -> {
                if (rType.getSort() <= Type.DOUBLE) {
                    // 拆箱
                    result.load(methodBody);
                    switch (rType.getSort()) {
                        case Type.BYTE:
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "unwrapByte", RuntimeUtil.UNWRAP_BYTE_DESC, false);
                            break;
                        case Type.SHORT:
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "unwrapShort", RuntimeUtil.UNWRAP_SHORT_DESC, false);
                            break;
                        case Type.INT:
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "unwrapInt", RuntimeUtil.UNWRAP_INT_DESC, false);
                            break;
                        case Type.LONG:
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.RUNTIME_UTIL_OWNER, "unwrapLong", RuntimeUtil.UNWRAP_LONG_DESC, false);
                            break;
                    }
                } else {
                    // 强转
                    result.load(methodBody);
                    mv.visitTypeInsn(Opcodes.CHECKCAST, rType.getInternalName());
                }
            });
        } else {
            result.load(methodBody);
        }
    }

    @Override
    public void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        getter.mhReassign(methodBody, lookupMember, mhMember, objVar);
    }
}
