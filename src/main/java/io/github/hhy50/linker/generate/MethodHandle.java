package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.ConditionJumpAction;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;
import io.github.hhy50.linker.generate.getter.TargetFieldGetter;
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
        if (this.defined) return;
        define0(classImplBuilder);
        this.defined = true;
    }

    /**
     * Define 0.
     *
     * @param classImplBuilder the class impl builder
     */
    protected void define0(InvokeClassImplBuilder classImplBuilder) {

    }

    /**
     * Invoke var inst.
     *
     * @param methodBody the method body
     * @return the var inst
     */
    public abstract VarInst invoke(MethodBody methodBody);

    /**
     * Init static method handle.
     *
     * @param clinit      the clinit
     * @param mhMember    the mh member
     * @param lookupClass the lookup class
     * @param fieldName   the field name
     * @param methodType  the method type
     * @param isStatic    the is static
     */
    protected void initStaticMethodHandle(MethodBody clinit, MethodHandleMember mhMember, Action lookupClass, String fieldName, Type methodType, boolean isStatic) {

    }

    /**
     * Mh reassign.
     *
     * @param methodBody  the method body
     * @param lookupClass the lookup class
     * @param mhMember    the mh member
     * @param objVar      the obj var
     */
    protected abstract void mhReassign(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar);

    /**
     * Check look class.
     *
     * @param body        the body
     * @param lookupClass the lookup class
     * @param varInst     the var inst
     * @param prevGetter  the prev getter
     */
    protected void checkLookClass(MethodBody body, ClassTypeMember lookupClass, VarInst varInst, Getter<?> prevGetter) {
        body.append(new ConditionJumpAction(
                must(notNull(varInst),
                        any(isNull(lookupClass), notEq(varInst.getThisClass(), lookupClass))),
                lookupClass.store(varInst.getThisClass()),
                null
        ));
        if (prevGetter instanceof TargetFieldGetter) {
            final ClassTypeMember targetClass = ((TargetFieldGetter) prevGetter).getTargetClass();
            if (targetClass != null) {
                body.append(new ConditionJumpAction(
                        isNull(lookupClass),
                        lookupClass.store(targetClass),
                        null
                ));
            }

            final Type defaultType = ((TargetFieldGetter) prevGetter).getTargetType();
            body.append(new ConditionJumpAction(
                    isNull(lookupClass),
                    lookupClass.store(loadClass(defaultType)),
                    null
            ));
        }
    }

    /**
     * Static check class.
     *
     * @param body          the body
     * @param lookupClass   the lookup class
     * @param prevFieldName the prev field name
     * @param prevLookup    the prev lookup
     */
    protected void staticCheckClass(MethodBody body, ClassTypeMember lookupClass, String prevFieldName, ClassTypeMember prevLookup) {
        body.append(new ConditionJumpAction(
                isNull(lookupClass),
                lookupClass.store(new MethodInvokeAction(Runtime.FIND_FIELD).setArgs(prevLookup, LdcLoadAction.of(prevFieldName))),
                null
        ));
    }


    /**
     * Check method handle.
     *
     * @param methodBody  the method body
     * @param lookupClass the lookup class
     * @param mhMember    the mh member
     * @param objVar      the obj var
     */
    protected void checkMethodHandle(MethodBody methodBody, ClassTypeMember lookupClass, MethodHandleMember mhMember, VarInst objVar) {
        methodBody.append(mhMember.ifNull(body -> mhReassign(body, lookupClass, mhMember, objVar)));
    }

    /**
     * Gets class load action.
     *
     * @param type the type
     * @return the class load action
     */
    protected Action loadClass(Type type) {
        return new DynamicClassLoad(type);
    }

    /**
     * The type Dynamic class load.
     */
    protected class DynamicClassLoad implements Action {
        private Type type;
        private LocalVarInst clazzVar;

        /**
         * Instantiates a new Dynamic class load.
         *
         * @param type the type
         */
        public DynamicClassLoad(Type type) {
            this.type = type;
        }

        @Override
        public void apply(MethodBody body) {
            if (AsmUtil.isPrimitiveType(type)) {
                LdcLoadAction.of(type).load(body);
                return;
            }

            if ((this.clazzVar = body.getClassObjCache(type)) == null) {
                AsmClassBuilder classBuilder = body.getClassBuilder();
                Action cl = LdcLoadAction.of(AsmUtil.getType(classBuilder.getClassName()))
                        .invokeMethod(MethodDescriptor.GET_CLASS_LOADER);
                this.clazzVar = body.newLocalVar(Type.getType(Class.class), new MethodInvokeAction(Runtime.GET_CLASS)
                        .setArgs(cl, LdcLoadAction.of(type.getClassName())));

                if (body.getMethodBuilder().isStatic()) {
                    body.putClassObjCache(type, this.clazzVar);
                }
            }
            clazzVar.loadToStack();
        }
    }
}
