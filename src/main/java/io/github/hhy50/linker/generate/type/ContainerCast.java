package io.github.hhy50.linker.generate.type;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;


public class ContainerCast implements TypeCast {

    @Override
    public VarInst cast(MethodBody methodBody, VarInst varInst, Type expectType) {
        return varInst;
    }
}
