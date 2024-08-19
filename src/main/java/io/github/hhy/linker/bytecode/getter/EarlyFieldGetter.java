package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHolder;
import io.github.hhy.linker.bytecode.vars.*;
import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy.linker.asm.AsmUtil.adaptLdcClassType;

public class EarlyFieldGetter extends Getter<EarlyFieldRef> {
    public final FieldRef prev;
    public final Type methodType;
    public MethodHandleMember mhMember;
    private MethodHolder methodHolder;
    private boolean inited = false;

    public EarlyFieldGetter(String implClass, EarlyFieldRef fieldRef, Type methodType) {
        super(fieldRef);
        this.prev = field.getPrev();
        this.methodType = methodType;
        this.methodHolder = new MethodHolder(ClassUtil.className2path(implClass), "get_" + field.getFullName(), this.methodType.getDescriptor());
    }

    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        this.prev.getter.define(classImplBuilder);

        // 定义上一层字段的lookup, 必须要用declaredType
        this.lookupMember = classImplBuilder.defineLookup(Opcodes.ACC_PUBLIC|Opcodes.ACC_STATIC, prev.getType());

        // 定义当前字段的getter mh
        this.mhMember = classImplBuilder.defineStaticMethodHandle(field.getGetterName(), methodType);

        // 如果上层也是确定好的类型, 直接初始化
        if (this.prev instanceof EarlyFieldRef) {
            EarlyFieldRef prev = (EarlyFieldRef) this.prev;
            this.lookupMember.staticInit(classImplBuilder.getClinit(), this.prev.getType());

            LookupMember declaredLookup = this.lookupMember;
            if (field.declaredType != field.getType()) {
                LookupMember lookup = classImplBuilder
                        .defineLookup(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, field.declaredType);
                lookup.staticInit(classImplBuilder.getClinit(), field.declaredType);
                declaredLookup = lookup;
            }

            initStaticMethodHandle(classImplBuilder, this.mhMember, this.lookupMember,
                    prev.realType, this.field.fieldName, Type.getMethodType(field.declaredType), field.isStatic());
            this.inited = true;
        }

        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.methodName, methodHolder.desc, null, "").accept(mv -> {
            MethodBody methodBody = new MethodBody(mv, methodType);
            ObjectVar objVar = prev.getter.invoke(methodBody);
            if (!inited) {
                checkLookup(methodBody, lookupMember, mhMember, objVar);
                checkMethodHandle(methodBody, lookupMember, mhMember, objVar);
            }
            // mh.invoke(obj)
            ObjectVar result = field.isStatic() ? mhMember.invokeStatic(methodBody) : mhMember.invokeInstance(methodBody, objVar);
            result.load(methodBody);
            AsmUtil.areturn(mv, methodType.getReturnType());
        });
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        FieldVar objectVar = new FieldVar(methodBody.lvbIndex++, methodType.getReturnType(), field.fieldName);
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, methodHolder.owner, methodHolder.methodName, methodHolder.desc, false);
            objectVar.store(methodBody);
        });
        return objectVar;
    }

    @Override
    protected void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember,
                                          Type ownerType, String fieldName, Type methodType, boolean isStatic) {
        MethodBody clinit = classImplBuilder.getClinit();
        clinit.append(mv -> {
            // mh = lookup.findGetter(ArrayList.class, "elementData", Object[].class);
            lookupMember.load(clinit); // lookup
            mv.visitLdcInsn(ownerType); // ArrayList.class
            mv.visitLdcInsn(fieldName); // 'elementData'
            adaptLdcClassType(mv, methodType.getReturnType()); // Object[].class

            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, LookupVar.OWNER, isStatic ? "findStaticGetter" : "findGetter", LookupVar.FIND_GETTER_DESC, false);
            mhMember.store(clinit);
        });
    }
}
