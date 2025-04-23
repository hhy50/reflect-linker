package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

/**
 * The type Create linker collect action.
 */
public class CreateLinkerCollectAction extends CreateLinkerAction {

    /**
     * Instantiates a new Create linker collect action.
     *
     * @param linkerType the linker type
     * @param obj        the obj
     */
    public CreateLinkerCollectAction(Type linkerType, VarInst obj) {
        super(linkerType, obj);
    }


    @Override
    public void apply(MethodBody body) {
        body.append(obj.ifNull(Actions.returnNull(), new MethodInvokeAction(MethodDescriptor.LINKER_FACTORY_CREATE_LINKER_COLLECT)
                .setArgs(LdcLoadAction.of(linkerType), obj)));
    }
}
