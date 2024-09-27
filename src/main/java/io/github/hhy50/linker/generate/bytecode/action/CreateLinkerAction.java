package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

/**
 * The type Create linker action.
 */
public class CreateLinkerAction implements Action {

    private final Type linkerType;
    private final VarInst obj;

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
    public void apply(MethodBody body) {
        body.append(obj.ifNull(Action.returnNull(), new MethodInvokeAction(MethodHolder.LINKER_FACTORY_CREATE_LINKER)
                .setArgs(LdcLoadAction.of(linkerType), obj)));;
    }
}
