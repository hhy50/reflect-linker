package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.ClassTypeVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;
import org.objectweb.asm.Type;

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
     * @param argsAction
     * @return the var inst
     */
    public abstract ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction);

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
     */
    protected Action initRuntimeMethodHandle(MethodHandleMember mhMember, ClassTypeMember lookupClass) {
        return Actions.empty();
    }

    /**
     * Check look class.
     *
     * @param lookupClass the lookup class
     * @param varInst     the var inst
     */
    protected Action checkLookClass(ClassTypeMember lookupClass, VarInst varInst, VarInst prevLookupClass, VarInst defaultType) {
        Action action = new ConditionJumpAction(
                must(notNull(varInst),
                        any(isNull(lookupClass), notEq(varInst.getThisClass(), lookupClass))),
                lookupClass.store(varInst.getThisClass()),
                null
        );
        if (prevLookupClass != null) {
            // runtime
            action = action.andThen(new ConditionJumpAction(
                    isNull(lookupClass),
                    lookupClass.store(prevLookupClass.cast(Type.getType(Class.class))),
                    null
            ));
        }
        if (defaultType != null) {
            action = action.andThen(new ConditionJumpAction(
                    must(isNull(lookupClass), notNull(defaultType)),
                    lookupClass.store(new ClassLoadAction(defaultType.cast(Type.getType(String.class)))),
                    null
            ));
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
    protected Action staticCheckClass(ClassTypeMember lookupClass, String prevFieldName, Action prevLookup) {
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
     */
    protected Action checkMethodHandle(ClassTypeMember lookupClass, MethodHandleMember mhMember) {
        return mhMember.ifNull(initRuntimeMethodHandle(mhMember, lookupClass));
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
                        classCache = new CachedClassLoad(type);
                        body.putClassObjCache(type, classCache);
                    }
                    classCache.getLookup().apply(body);
                };
            }

            @Override
            public void apply(MethodBody body) {
                ClassTypeVarInst classCache = body.getClassObjCache(type);
                if (classCache == null) {
                    classCache = new CachedClassLoad(type);
                    body.putClassObjCache(type, classCache);
                }
                classCache.apply(body);
            }
        };
    }

    /**
     * The type Dynamic class load.
     */
    protected class CachedClassLoad extends ClassLoadAction {
        private LocalVarInst clazzVar;
        private LocalVarInst lookupVar;

        /**
         * Instantiates a new Dynamic class load.
         *
         * @param type the type
         */
        public CachedClassLoad(Type type) {
            super(type);
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
            if (this.clazzVar == null) {
                this.clazzVar = body.newLocalVar(VarInst.wrap(this.load(), Type.getType(Class.class)));
            }
            clazzVar.loadToStack();
        }
    }
}
