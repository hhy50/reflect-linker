package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;

import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.mapOwnerAndArgs;
import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.of;


/**
 * The type Runtime method invoker.
 */
public class RuntimeMethodInvoker extends Invoker<RuntimeMethodRef> {

    private String fullName;

    private Boolean isDesignateStatic;

    private Type mhType;

    private MethodDescriptor rmd;

    private boolean autolinked;

    /**
     * Instantiates a new Runtime method invoker.
     *
     * @param methodRef the method ref
     */
    public RuntimeMethodInvoker(RuntimeMethodRef methodRef) {
        super(null, methodRef.getName(), methodRef.getLookupType(), methodRef.getSuperClass());

        this.isDesignateStatic = methodRef.isDesignateStatic();
        this.fullName = methodRef.getFullName();

        Type[] argumentTypes = super.lookupType.getArgumentTypes();
        Arrays.fill(argumentTypes, ObjectVar.TYPE);

        this.rmd = MethodDescriptor.of("invoke_" + this.fullName, TypeUtil.appendArgs(Type.getMethodType(ObjectVar.TYPE, argumentTypes), ObjectVar.TYPE, true));
        if (methodRef.isAutolink()) {
            // 因为是根据形参寻找方法，但是形参是链接器，所以找不到具体方法，查找逻辑在io.github.hhy50.linker.runtime.Runtime.findMethod
            // 约定将参数0设置为Autolink，以保证使用实参来查找方法
//            super.lookupType = Type.getMethodType(lookupType.getReturnType(), Type.getType(Object[].class));
            argumentTypes = new Type[]{ Type.getType(Object[].class) };
            super.lookupType = Type.getMethodType(lookupType.getReturnType(), Type.getType(Autolink.class));
            this.autolinked = true;
        }
        this.mhType = Type.getMethodType(ObjectVar.TYPE, argumentTypes);
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        ClassTypeMember lookupClass = classImplBuilder.defineLookupClass(this.fullName);
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(this.fullName, this.mhType);

        ChainAction<VarInst> invoker = mapOwnerAndArgs(of(MethodBody::getArgs), (ownerVar, args) -> {
            Action loadArgs = Actions.of(args);
            if (this.autolinked) {
                loadArgs = Actions.asArray(ObjectVar.TYPE, args);
            }
            Action action = isDesignateStatic != null ? (isDesignateStatic ? mhMember.invokeStatic(loadArgs)
                    : mhMember.invokeInstance(ownerVar, loadArgs))
                    : mhMember.invokeOfNull(ownerVar, loadArgs);

            return VarInst.wrap(action, rmd.getReturnType());
        });

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, this.rmd.getMethodName(), this.rmd.getType(), null)
                .intercept(of(() -> Args.of(0))
                        .then(ownerVar -> checkLookClass(lookupClass, ownerVar, null))
                        .then(ownerVar -> {
//                            ClassTypeMember prevLookupClass = ownerGetter.lookupClass;
//                            if (prevLookupClass != null) {
//                                return staticCheckClass(lookupClass, owner.fieldName, prevLookupClass);
//                            }
                            return null;
                        })
                        .then(ownerVar -> checkMethodHandle(lookupClass, mhMember))
                        .andThen(invoker.areturn()));
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        return of(() -> new SmartMethodInvokeAction(this.rmd)
                .setInstance(LoadAction.LOAD0)
                .setArgs(argsAction));
    }
}
