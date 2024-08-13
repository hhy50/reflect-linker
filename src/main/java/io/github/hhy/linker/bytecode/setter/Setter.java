package io.github.hhy.linker.bytecode.setter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHandle;
import io.github.hhy.linker.bytecode.MethodRef;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.RuntimeField;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class Setter extends MethodHandle {
    protected boolean defined = false;
    protected final RuntimeField field;
    public final RuntimeField prev;
    protected final Type methodType;
    protected LookupMember lookupMember;
    protected MethodHandleMember mhMember;
    protected MethodRef methodRef;

    public Setter(String implClass, RuntimeField field, Type methodType) {
        this.field = field;
        this.prev = field.getPrev();
        this.methodType = methodType;
        this.methodRef = new MethodRef(ClassUtil.className2path(implClass), "set_" + field.getFullName(), methodType.getDescriptor());
    }

    public final void define(InvokeClassImplBuilder classImplBuilder) {
        if (defined) return;
        this.prev.getter.define(classImplBuilder);
        // 先定义上一层字段的lookup
        this.lookupMember = classImplBuilder.defineLookup(this.prev.getLookupName());
        // 定义当前字段的 setter_mh
        this.mhMember = classImplBuilder.defineMethodHandle(field.getSetterName(),
                AsmUtil.addArgsDesc(methodType, Type.getType("Ljava/lang/Object;"), true));
        // 定义当前字段的 setter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodRef.methodName, methodRef.desc, null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(mv, methodType);
                    ObjectVar objVar = prev.getter.invoke(methodBody);
//                    objVar.checkNullPointer(methodBody, field.getNullErrorVar());
                    // 校验lookup和mh
                    if (!lookupMember.memberName.equals(RuntimeField.TARGET.getLookupName())) {
//                        staticCheckLookup(methodBody, lookupMember, objVar);
                        checkLookup(methodBody, lookupMember, mhMember, objVar);
                    }
                    checkMethodHandle(methodBody, lookupMember, mhMember, objVar);
                    // mh.invoke(obj, value)
                    mhMember.invoke(methodBody, objVar, methodBody.getArg(0));
                    AsmUtil.areturn(mv, Type.VOID_TYPE);
                });

        this.defined = true;
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            methodBody.loadArgs();
            mv.visitMethodInsn(INVOKEVIRTUAL, methodRef.owner, methodRef.methodName, methodRef.desc, false);
        });
        return null;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        methodBody.append(mv -> {
            lookupMember.load(methodBody); // this.lookup
            objVar.thisClass(methodBody); // obj.class
            mv.visitLdcInsn(this.field.fieldName); // 'field'
            mv.visitMethodInsn(INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "findSetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/invoke/MethodHandle;", false);
            mhMember.store(methodBody);
        });
    }
}
