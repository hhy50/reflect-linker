package io.github.hhy.linker.generate.setter;

import io.github.hhy.linker.constant.Lookup;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.MethodHandle;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.runtime.Runtime;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy.linker.asm.AsmUtil.adaptLdcClassType;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public abstract class Setter<T extends FieldRef> extends MethodHandle {

    protected final T field;
    protected MethodHolder methodHolder;

    protected Type methodType;

    public Setter(String implClass, T field) {
        this.field = field;
        this.methodType = Type.getMethodType(Type.VOID_TYPE, field.getType());
        this.methodHolder = new MethodHolder(ClassUtil.className2path(implClass), "set_"+field.getFullName(), this.methodType.getDescriptor());
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            methodBody.loadArgs();
            mv.visitMethodInsn(INVOKEVIRTUAL, methodHolder.getOwner(), methodHolder.getMethodName(), methodHolder.getDesc(), false);
        });
        return null;
    }

    @Override
    protected void mhReassign(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember mhMember, VarInst objVar) {
        methodBody.append(mv -> {
            lookupMember.load(methodBody); // this.lookup
            mv.visitLdcInsn(this.field.fieldName); // 'field'
            mv.visitMethodInsn(INVOKESTATIC, Runtime.OWNER, "findSetter", Runtime.FIND_SETTER_DESC, false);
            mhMember.store(methodBody);
        });
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

            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Lookup.OWNER, isStatic ? "findStaticSetter" : "findSetter", Lookup.FIND_GETTER_DESC, false);
            mhMember.store(clinit);
        });
    }
}
