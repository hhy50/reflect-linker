package io.github.hhy50.linker.generate.invoker;


import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.ClassTypeFieldVarInst;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.MethodHandleFieldVarInst;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.bytecode.vars.VarInstWithLookup;
import io.github.hhy50.linker.tools.FieldTypeUniqueKey;
import io.github.hhy50.linker.util.RandomUtil;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.BiFunction;

import static io.github.hhy50.linker.generate.bytecode.action.ChainAction.of;

/**
 * The type Field ops method handler.
 */
public abstract class FieldOpsMethodHandler extends MethodHandle {
    /**
     * The Field name.
     */
    protected final String fieldName;

    /**
     * The Ops field type.
     */
    protected final Type opsFieldType;

    /**
     * 运行时调用的方法
     */
    protected MethodDescriptor rmd;

    /**
     * The Inline mh invoker.
     */
    protected BiFunction<VarInst, VarInst[], VarInst> inlineMhInvoker;

    /**
     * The Lookup class.
     */
    public ClassTypeFieldVarInst lookupClassFieldVarInst;

    /**
     * Instantiates a new Field ops method handler.
     *
     * @param fieldName    the field name
     * @param opsFieldType the opsFieldType
     */
    public FieldOpsMethodHandler(String fieldName, Type opsFieldType) {
        this.fieldName = fieldName;
        this.opsFieldType = opsFieldType;
    }

    /**
     * defineRuntimeMethod
     *
     * @param classImplBuilder  the class impl builder
     * @param isDesignateStatic the is designate static
     */
    protected void defineRuntimeMethod(InvokeClassImplBuilder classImplBuilder, Boolean isDesignateStatic) {
        String rname = "getter_";
        if (this.opsFieldType.getReturnType() == Type.VOID_TYPE) {
            rname = "setter_";
        }
        rname += this.fieldName + RandomUtil.getRandomString(5);
        this.rmd = MethodDescriptor.of(rname, TypeUtil.appendArgs(this.opsFieldType, Type.getType(Object[].class), true));

        MethodHandleFieldVarInst mhFieldVarInst = classImplBuilder.defineMethodHandle(this.fieldName, this.opsFieldType);
        BiFunction<VarInst, VarInst[], VarInst> invoker = (ownerVar, args) -> {
            Action action = isDesignateStatic != null ?
                    (isDesignateStatic ? mhFieldVarInst.invokeStatic(args) : mhFieldVarInst.invokeInstance(ownerVar, args))
                    : mhFieldVarInst.invokeOfNull(ownerVar, args);
            return VarInst.wrap(action, opsFieldType.getReturnType());
        };

        classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, this.rmd.getMethodName(), this.rmd.getType(), null)
                .intercept(of(() -> new RuntimeMethodInvoker.RuntimeOwnerAndType(LoadAction.aload(1)))
                        .then(holder -> checkLookClass(this.lookupClassFieldVarInst, holder.owner, holder.ownerType, holder.defaultType))
                        .then(__ -> checkMethodHandle(this.lookupClassFieldVarInst, mhFieldVarInst))
                        .mapBody((body, holder) -> {
                            VarInst[] realArgs = Arrays.copyOfRange(body.getArgs(), 1, body.getArgs().length);
                            return invoker.apply(holder.owner, realArgs);
                        })
                        .map(ret -> {
                            if (ret.getType() == Type.VOID_TYPE) {
                                return ret;
                            }
                            return new VarInstWithLookup(ret, this.lookupClassFieldVarInst);
                        })
                        .areturn()
                );
    }

    /**
     * Define method.
     *
     * @param classImplBuilder classImplBuilder
     * @param fieldRef         fieldRef
     */
    protected void defineMethod(InvokeClassImplBuilder classImplBuilder, EarlyFieldRef fieldRef) {
        Field reflect = fieldRef.getReflect();
        boolean isStatic = Modifier.isStatic(reflect.getModifiers());
        Type lookupClass = Type.getType(reflect.getDeclaringClass());

        Object uniqueKey;
        if (this.opsFieldType.getReturnType() == Type.VOID_TYPE) {
            uniqueKey = FieldTypeUniqueKey.withSetter(reflect);
        } else {
            uniqueKey = FieldTypeUniqueKey.withGetter(reflect);
        }

        // init methodHandle
        MethodBody clinit = classImplBuilder.getClinit();
        MethodHandleFieldVarInst mhFieldVarInst = classImplBuilder.defineStaticMethodHandle(uniqueKey, this.fieldName, null, this.opsFieldType);
        clinit.append(initStaticMethodHandle(mhFieldVarInst, loadClass(lookupClass), isStatic));
        if (isStatic) {
            this.inlineMhInvoker = (__, args) -> mhFieldVarInst.invokeStatic(args);
        } else {
            this.inlineMhInvoker = mhFieldVarInst::invokeInstance;
        }
    }
}
