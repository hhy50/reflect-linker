package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

/**
 * The type Create linker action.
 */
public class CreateLinkerAction extends VarInst  {

    /**
     * The Linker type.
     */
    protected final Type linkerType;
    /**
     * The Obj.
     */
    protected final VarInst obj;

    /**
     * Instantiates a new Create linker action.
     *
     * @param linkerType the linker type
     * @param obj        the obj
     */
    public CreateLinkerAction(Type linkerType, VarInst obj) {
        this.linkerType = linkerType;
        this.obj = obj;
    }


    @Override
    public Action load() {
        return new MethodInvokeAction(MethodDescriptor.LINKER_FACTORY_CREATE_LINKER)
                .setArgs(LdcLoadAction.of(linkerType), obj);
    }

    @Override
    public Type getType() {
        return linkerType;
    }
}
