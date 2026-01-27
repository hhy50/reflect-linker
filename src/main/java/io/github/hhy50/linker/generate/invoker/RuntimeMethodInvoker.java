package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.ArrayVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.bytecode.vars.VarInstWithLookup;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.function.BiFunction;

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

        Type genericType = methodRef.getGenericType();

        this.rmd = MethodDescriptor.of("invoke_" + this.fullName, TypeUtil.appendArgs(genericType, Type.getType(Object[].class), true));
        if (methodRef.isAutolink()) {
            // 因为是根据形参寻找方法，但是形参是链接器，所以找不到具体方法，查找逻辑在io.github.hhy50.linker.runtime.Runtime.findMethod
            // 约定将参数0设置为Autolink，以保证使用实参来查找方法
//            super.lookupType = Type.getMethodType(lookupType.getReturnType(), Type.getType(Object[].class));
            genericType = Type.getMethodType(genericType.getReturnType(), Type.getType(Object[].class));
            super.lookupType = Type.getMethodType(lookupType.getReturnType(), Type.getType(Autolink.class));
            this.autolinked = true;
        }
        this.mhType = genericType;
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        ClassTypeMember lookupClass = classImplBuilder.defineLookupClass(this.fullName);
        MethodHandleMember mhMember = classImplBuilder.defineMethodHandle(this.fullName, this.mhType);

        BiFunction<VarInst, VarInst[], VarInst> invoker = (ownerVar, args) -> {
            Action loadArgs = Actions.of(args);
            if (this.autolinked) {
                loadArgs = Actions.asArray(ObjectVar.TYPE, args);
            }
            Action action = isDesignateStatic != null ? (isDesignateStatic ? mhMember.invokeStatic(loadArgs) : mhMember.invokeInstance(ownerVar, loadArgs))
                    : mhMember.invokeOfNull(ownerVar, loadArgs);

            return VarInst.wrap(action, rmd.getReturnType());
        };

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, this.rmd.getMethodName(), this.rmd.getType(), null)
                .intercept(of(() -> new RuntimeOwnerAndType(LoadAction.aload(1)))
                        .then(holder -> checkLookClass(lookupClass, holder.owner, holder.ownerType, holder.defaultType))
                        .then(ownerVar -> {
                            return null;
                        })
                        .then(__ -> checkMethodHandle(lookupClass, mhMember))
                        .mapBody((body, holder) -> {
                            VarInst[] realArgs = Arrays.copyOfRange(body.getArgs(), 1, body.getArgs().length);
                            return invoker.apply(holder.owner, realArgs);
                        })
                        .map(ret -> new VarInstWithLookup(ret, lookupClass))
                        .areturn()
                );
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        return of(() -> new SmartMethodInvokeAction(this.rmd)
                .setInstance(LoadAction.LOAD0)
                .setArgs(argsAction.map(args -> {
                    Action owner = args[0];
                    Action lookupClass = null;
                    Action defaultType = null;

                    if (args[0] instanceof VarInstWithLookup) {
                        lookupClass = ((VarInstWithLookup) args[0]).getLookupClass();
                        Type dt = ((VarInstWithLookup) args[0]).defaultType();
                        if (dt != null) {
                            defaultType = LdcLoadAction.of(dt.getClassName());
                        }
                    }
                    lookupClass = lookupClass == null ? Actions.loadNull() : lookupClass;
                    defaultType = defaultType == null ? Actions.loadNull() : defaultType;
                    // 重写参数
                    args[0] = VarInst.wrap(Actions.asArray(ObjectVar.TYPE, owner, lookupClass, defaultType), Type.getType(Object[].class));
                    return args;
                })));
    }


    static class RuntimeOwnerAndType  {
        VarInst owner;
        VarInst ownerType;
        VarInst defaultType;

        public RuntimeOwnerAndType(Action arg0) {
            ArrayVarInst arrayVarInst = new ArrayVarInst(VarInst.wrap(arg0, Type.getType(Object[].class)));
            this.owner = arrayVarInst.index(0);
            this.ownerType = arrayVarInst.index(1);
            this.defaultType = arrayVarInst.index(2);
        }
    }
}
