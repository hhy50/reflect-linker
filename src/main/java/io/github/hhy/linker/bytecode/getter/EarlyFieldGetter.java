package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodRef;
import io.github.hhy.linker.bytecode.vars.LookupMember;
import io.github.hhy.linker.bytecode.vars.LookupVar;
import io.github.hhy.linker.bytecode.vars.MethodHandleMember;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;

public class EarlyFieldGetter extends Getter {
    private static final Type DEFAULT_METHOD_TYPE = Type.getType("()Ljava/lang/Object;");
    public final FieldRef prev;
    public final Type methodType;
    public MethodHandleMember mhMember;
    private MethodRef methodRef;
    private boolean inited = false;

    public EarlyFieldGetter(String implClass, EarlyFieldRef fieldRef, Type methodType) {
        super(fieldRef);
        this.prev = field.getPrev();
        this.methodType = methodType == null ? DEFAULT_METHOD_TYPE : methodType;
        this.methodRef = new MethodRef(ClassUtil.className2path(implClass), "get_" + field.getFullName(), this.methodType.getDescriptor());
    }

    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {

        this.prev.getter.define(classImplBuilder);
        // 先定义上一层字段的lookup
        this.lookupMember = classImplBuilder.defineLookup(this.prev);
        // 定义当前字段的mh
        this.mhMember = classImplBuilder.defineMethodHandle(field.getGetterName(), methodType);

        // 如果上层也是确定好的类型, 直接初始化
        if (this.prev instanceof EarlyFieldRef) {
            initStaticLookup(classImplBuilder, this.lookupMember, (EarlyFieldRef) this.prev);
            initStaticMethodHandle(classImplBuilder, this.lookupMember, this.field.fieldName, ((EarlyFieldRef) this.field).getType());
            this.inited = true;
        }

        // 定义当前字段的getter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodRef.methodName, methodRef.desc, null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(mv, methodType);
                    ObjectVar objVar = prev.getter.invoke(methodBody);
                    if (!inited) {
                        // 校验lookup和mh
                        Getter prev = this.prev.getter;
                        checkLookup(methodBody, lookupMember, mhMember, objVar);
                    }
                    checkMethodHandle(methodBody, lookupMember, mhMember, objVar);

                    // mh.invoke(obj)
                    ObjectVar result = mhMember.invoke(methodBody, objVar);
                    result.load(methodBody);
                    AsmUtil.areturn(mv, methodType.getReturnType());
                });
    }

    private void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, LookupMember lookupMember, String fieldName, Field field) {
        MethodBody clinit = classImplBuilder.getClinit();
        clinit.append(mv -> {
            lookupMember.load(clinit);
            if ((field.getModifiers() & Opcodes.ACC_STATIC) > 0) {
            } else {

            }
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, LookupVar.DESCRIPTOR, "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false);

            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
            lookupMember.store(clinit);
        });
    }

    private static void initStaticLookup(InvokeClassImplBuilder classImplBuilder, LookupMember lookupMember, EarlyFieldRef fieldRef) {
        MethodBody clinit = classImplBuilder.getClinit();
        clinit.append(mv -> {
            mv.visitLdcInsn(Type.getType(fieldRef.getType()));
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
            lookupMember.store(clinit);
        });
    }

    private static boolean initStaticMethodHandle(EarlyFieldRef fieldRef, InvokeClassImplBuilder classImplBuilder) {
        if (fieldRef.getPrev() != null && fieldRef.getPrev() instanceof EarlyFieldRef) {
            MethodBody clinit = classImplBuilder.getClinit();
            clinit.append(mv -> {
                LookupMember prevLookup = classImplBuilder.defineLookup(fieldRef.getPrev());
                prevLookup.load(clinit);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
                prevLookup.store(clinit);
            });
            return true;
        }
        return false;
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        ObjectVar objectVar = new ObjectVar(methodBody.lvbIndex++, methodType.getReturnType().getDescriptor());
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, methodRef.owner, methodRef.methodName, methodRef.desc, false);
            objectVar.store(methodBody);
        });
        return objectVar;
    }
}
