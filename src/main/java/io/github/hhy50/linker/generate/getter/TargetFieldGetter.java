package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.Member;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;


/**
 * The type Target field getter.
 */
public class TargetFieldGetter extends Getter<EarlyFieldRef> {
    private final Member targetObj;

    /**
     * Instantiates a new Target field getter.
     *
     * @param implClass      the impl class
     * @param targetFieldRef the target field ref
     */
    public TargetFieldGetter(String implClass, EarlyFieldRef targetFieldRef) {
        super(implClass, targetFieldRef);
        this.targetObj = Member.of(targetFieldRef.fieldName, ObjectVar.TYPE);
    }


    @Override
    public VarInst invoke(MethodBody methodBody) {
        return methodBody.newLocalVar(field.getType(), field.fieldName, this.targetObj);
    }

    /**
     * Gets target type.
     *
     * @return the target type
     */
    public Type getTargetType() {
        return this.field.getType();
    }
}
