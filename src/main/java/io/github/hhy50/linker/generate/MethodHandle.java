package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.TargetFieldGetter;
import io.github.hhy50.linker.generate.invoker.Getter;
import io.github.hhy50.linker.runtime.Runtime;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.util.Objects;

import static io.github.hhy50.linker.generate.bytecode.action.Condition.*;

/**
 * The type Method handle.
 */
public abstract class MethodHandle {

    /**
     * The Defined.
     */
    protected boolean defined = false;

    /**
     * Define.
     *
     * @param classImplBuilder the class impl builder
     */
    public final void define(InvokeClassImplBuilder classImplBuilder) {
        if (!this.defined) {
            define0(classImplBuilder);
        }
        this.defined = true;
    }

    /**
     * Define 0.
     *
     * @param classImplBuilder the class impl builder
     */
    protected abstract void define0(InvokeClassImplBuilder classImplBuilder);

    /**
     * Invoke var inst.
     *
     * @param varInstChain
     * @param argsChainAction
     * @return the var inst
     */
    public abstract ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, ChainAction<VarInst[]> argsChainAction);

    /**
     * Init static method handle.
     *
     * @param mhMember
     * @param lookupClass the lookup class
     * @param isStatic    the is static
     */
    protected Action initStaticMethodHandle(MethodHandleMember mhMember, ClassTypeVarInst lookupClass, boolean isStatic) {
        return Actions.empty();
    }

    /**
     * Mh reassign.
     *
     * @param mhMember    the mh member
     * @param lookupClass the lookup class
     * @param mhType
     */
    protected Action initRuntimeMethodHandle(MethodHandleMember mhMember, ClassTypeMember lookupClass, Type mhType) {
        return Actions.empty();
    }

    /**
     * Check look class.
     *
     * @param lookupClass the lookup class
     * @param varInst     the var inst
     * @param prevGetter  the prev getter
     */
    protected Action checkLookClass(ClassTypeMember lookupClass, VarInst varInst, Getter prevGetter) {
        Action action = new ConditionJumpAction(
                must(notNull(varInst),
                        any(isNull(lookupClass), notEq(varInst.getThisClass(), lookupClass))),
                lookupClass.store(varInst.getThisClass()),
                null
        );
        if (prevGetter instanceof TargetFieldGetter) {
            final ClassTypeMember targetClass = ((TargetFieldGetter) prevGetter).getTargetClass();
            if (targetClass != null) {
                // runtime
                action = action.andThen(new ConditionJumpAction(
                        isNull(lookupClass),
                        lookupClass.store(targetClass),
                        null
                ));
            } else {
                // not runtime
                Type defaultType = ((TargetFieldGetter) prevGetter).getTargetType();
                action = action.andThen(new ConditionJumpAction(
                        isNull(lookupClass),
                        lookupClass.store(loadClass(defaultType)),
                        null
                ));
            }
        }
        return action;
    }

    /**
     * Static check class.
     *
     * @param lookupClass   the lookup class
     * @param prevFieldName the prev field name
     * @param prevLookup    the prev lookup
     */
    protected Action staticCheckClass(ClassTypeMember lookupClass, String prevFieldName, ClassTypeMember prevLookup) {
        return new ConditionJumpAction(
                isNull(lookupClass),
                lookupClass.store(new MethodInvokeAction(Runtime.FIND_FIELD).setArgs(prevLookup, LdcLoadAction.of(prevFieldName))),
                null
        );
    }


    /**
     * Check method handle.
     *
     * @param lookupClass the lookup class
     * @param mhMember    the mh member
     * @param objVar      the obj var
     */
    protected Action checkMethodHandle(ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar) {
        return mhMember.ifNull(initRuntimeMethodHandle(mhMember, lookupClass, null));
    }

    /**
     * Gets class load action.
     *
     * @param type the type
     * @return the class load action
     */
    protected ClassTypeVarInst loadClass(Type type) {
        return new ClassTypeVarInst() {
            @Override
            public Action getLookup() {
                return body -> {
                    ClassTypeVarInst classCache = body.getClassObjCache(type);
                    if (classCache == null) {
                        classCache = new DynamicClassLoad(type);
                        body.putClassObjCache(type, classCache);
                    }
                    classCache.getLookup().apply(body);
                };
            }

            @Override
            public void apply(MethodBody body) {
                ClassTypeVarInst classCache = body.getClassObjCache(type);
                if (classCache == null) {
                    classCache = new DynamicClassLoad(type);
                    body.putClassObjCache(type, classCache);
                }
                classCache.apply(body);
            }
        };
    }

    /**
     * The type Dynamic class load.
     */
    protected class DynamicClassLoad implements ClassTypeVarInst {
        private Type type;
        private LocalVarInst clazzVar;
        private LocalVarInst lookupVar;

        /**
         * Instantiates a new Dynamic class load.
         *
         * @param type the type
         */
        public DynamicClassLoad(Type type) {
            Objects.requireNonNull(type);
            this.type = type;
        }

        @Override
        public Action getLookup() {
            return (body) -> {
                if (this.lookupVar == null) {
                    this.lookupVar = body.newLocalVar(new MethodInvokeAction(Runtime.LOOKUP).setArgs(this));
                }
                this.lookupVar.loadToStack();
            };
        }

        @Override
        public void apply(MethodBody body) {
            if (TypeUtil.isPrimitiveType(type)) {
                body.append(LdcLoadAction.of(type));
                return;
            }

            if (this.clazzVar == null) {
                AsmClassBuilder classBuilder = body.getClassBuilder();
                Action cl = LdcLoadAction.of(TypeUtil.getType(classBuilder.getClassName()))
                        .invokeMethod(MethodDescriptor.GET_CLASS_LOADER);
                this.clazzVar = body.newLocalVar(new MethodInvokeAction(Runtime.GET_CLASS)
                        .setArgs(cl, LdcLoadAction.of(type.getClassName())));
            }
            clazzVar.loadToStack();
        }
    }
}
