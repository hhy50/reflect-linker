package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodRef;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.LookupVar;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.runtime.Runtime;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy.linker.asm.AsmUtil.adaptLdcClassType;

public class EarlyFieldGetter extends Getter<EarlyFieldRef> {
    public final FieldRef prev;
    public final Type methodType;
    public MethodHandleMember mhMember;
    private MethodRef methodRef;
    private boolean inited = false;

    public EarlyFieldGetter(String implClass, EarlyFieldRef fieldRef, Type methodType) {
        super(fieldRef);
        this.prev = field.getPrev();
        this.methodType = methodType;
        this.methodRef = new MethodRef(ClassUtil.className2path(implClass), "get_"+field.getFullName(), this.methodType.getDescriptor());
    }

    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        this.prev.getter.define(classImplBuilder);
        // 先定义上一层字段的lookup
        this.lookupMember = classImplBuilder.defineLookup(this.prev);
        // 定义当前字段的mh
        this.mhMember = classImplBuilder.defineStaticMethodHandle(field.getGetterName(), methodType);

        // 如果上层也是确定好的类型, 直接初始化
        if (this.prev instanceof EarlyFieldRef) {
            initStaticLookup(classImplBuilder, this.lookupMember, ((EarlyFieldRef) this.prev).type);
            initStaticMethodHandle(classImplBuilder, this.mhMember, this.lookupMember,
                    ((EarlyFieldRef) this.prev).type, this.field.fieldName, methodType, field.isStatic());
            this.inited = true;
        }

        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodRef.methodName, methodRef.desc, null, "").accept(mv -> {
            MethodBody methodBody = new MethodBody(mv, methodType);
            ObjectVar objVar = prev.getter.invoke(methodBody);
            if (!inited) {
                checkLookup(methodBody, lookupMember, mhMember, objVar);
                checkMethodHandle(methodBody, lookupMember, mhMember, objVar);
            }
            // mh.invoke(obj)
            ObjectVar result = field.isStatic() ? mhMember.invokerStatic(methodBody, objVar) : mhMember.invokeInstance(methodBody, objVar);
            result.load(methodBody);
            AsmUtil.areturn(mv, methodType.getReturnType());
        });
    }

    private static void initStaticLookup(InvokeClassImplBuilder classImplBuilder, LookupMember lookupMember, Type type) {
        if (lookupMember.isTargetLookup()) return;

        MethodBody clinit = classImplBuilder.getClinit();
        clinit.append(mv -> {
            mv.visitLdcInsn(type);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Runtime.OWNER, "lookup", Runtime.LOOKUP_DESC, false);
            lookupMember.store(clinit);
        });
    }

    private void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember,
                                        Type ownerType, String fieldName, Type methodType, boolean isStatic) {
        MethodBody clinit = classImplBuilder.getClinit();
        clinit.append(mv -> {
            // mh = lookup.findGetter(ArrayList.class, "elementData", Object[].class);
            lookupMember.load(clinit); // lookup
            mv.visitLdcInsn(ownerType); // ArrayList.class,
            mv.visitLdcInsn(fieldName); // 'elementData'
            adaptLdcClassType(mv, methodType.getReturnType()); // Object[].class

            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, LookupVar.OWNER, isStatic ? "findStaticGetter" : "findGetter", LookupVar.FIND_GETTER_DESC, false);
            mhMember.store(clinit);
        });
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        ObjectVar objectVar = new ObjectVar(methodBody.lvbIndex++, methodType.getReturnType());
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, methodRef.owner, methodRef.methodName, methodRef.desc, false);
            objectVar.store(methodBody);
        });
        return objectVar;
    }
}
