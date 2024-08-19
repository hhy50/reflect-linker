package io.github.hhy.linker.bytecode.setter;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.MethodHandle;
import io.github.hhy.linker.bytecode.MethodHolder;
import io.github.hhy.linker.bytecode.getter.Getter;
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
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class Setter extends MethodHandle {
    protected boolean defined = false;
    protected final FieldRef field;
    public final FieldRef prev;
    protected final Type methodType;
    protected LookupMember lookupMember;
    protected MethodHandleMember mhMember;
    protected MethodHolder methodHolder;
    protected boolean isEarly;

    public Setter(String implClass, FieldRef field, Type methodType) {
        this.field = field;
        this.prev = field.getPrev();
        this.methodType = methodType;
        this.methodHolder = new MethodHolder(ClassUtil.className2path(implClass), "set_"+field.getFullName(), methodType.getDescriptor());
        this.isEarly = this.field instanceof EarlyFieldRef;
    }

    public final void define(InvokeClassImplBuilder classImplBuilder) {
        if (defined) return;
        this.prev.getter.define(classImplBuilder);
        // 先定义上一层字段的lookup
        this.lookupMember = classImplBuilder.defineLookup(this.prev);
        // 定义当前字段的 setter_mh
        this.mhMember = isEarly ? classImplBuilder.defineStaticMethodHandle(this.field.getSetterName(), methodType)
                : classImplBuilder.defineMethodHandle(this.field.getSetterName(), methodType);

        // 定义当前字段的 setter
        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, methodHolder.methodName, methodHolder.desc, null, "")
                .accept(mv -> {
                    MethodBody methodBody = new MethodBody(mv, methodType);
                    ObjectVar objVar = prev.getter.invoke(methodBody);
                    if (this.prev instanceof EarlyFieldRef) {
                        this.lookupMember.staticInit(classImplBuilder.getClinit(), this.prev.getType());
                    } else {
                        Getter prevGetter = this.prev.getter;
                        staticCheckLookup(methodBody, prevGetter.lookupMember, this.lookupMember, objVar, prevGetter.field);
                        checkLookup(methodBody, lookupMember, mhMember, objVar);
                    }
                    if (this.isEarly) {
                        EarlyFieldRef earlyField = (EarlyFieldRef) this.field;
                        initStaticMethodHandle(classImplBuilder, this.mhMember, this.lookupMember,
                                this.prev.getType(), this.field.fieldName, Type.getMethodType(Type.VOID_TYPE, earlyField.declaredType), earlyField.isStatic());

                        // mh.invoke(obj, value)
                        ObjectVar nil = ((EarlyFieldRef) this.field).isStatic()
                                ? mhMember.invokeStatic(methodBody, methodBody.getArg(0))
                                : mhMember.invokeInstance(methodBody, objVar, methodBody.getArg(0));
                    } else {
                        checkMethodHandle(methodBody, lookupMember, mhMember, objVar);
                        // mh.invoke(obj, value)
                        mhMember.invoke(methodBody, objVar, methodBody.getArg(0));
                    }
                    AsmUtil.areturn(mv, Type.VOID_TYPE);
                });
        this.defined = true;
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            methodBody.loadArgs();
            mv.visitMethodInsn(INVOKEVIRTUAL, methodHolder.owner, methodHolder.methodName, methodHolder.desc, false);
        });
        return null;
    }

    @Override
    protected void initStaticMethodHandle(InvokeClassImplBuilder classImplBuilder, MethodHandleMember mhMember, LookupMember lookupMember, Type ownerType, String fieldName, Type methodType, boolean isStatic) {
        MethodBody clinit = classImplBuilder.getClinit();
        clinit.append(mv -> {
            // mh = lookup.findSetter(ArrayList.class, "elementData", Object[].class);
            lookupMember.load(clinit); // lookup
            mv.visitLdcInsn(ownerType); // ArrayList.class,
            mv.visitLdcInsn(fieldName); // 'elementData'
            adaptLdcClassType(mv, methodType.getArgumentTypes()[0]); // Object[].class

            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, LookupVar.OWNER, isStatic ? "findStaticSetter" : "findSetter", LookupVar.FIND_GETTER_DESC, false);
            mhMember.store(clinit);
        });
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, ObjectVar objVar) {
        methodBody.append(mv -> {
            lookupMember.load(methodBody); // this.lookup
            mv.visitLdcInsn(this.field.fieldName); // 'field'
            mv.visitMethodInsn(INVOKESTATIC, Runtime.OWNER, "findSetter", Runtime.FIND_SETTER_DESC, false);
            mhMember.store(methodBody);
        });
    }
}
