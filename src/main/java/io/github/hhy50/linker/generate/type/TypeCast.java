package io.github.hhy50.linker.generate.type;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

/**
 * The interface Type cast.
 */
public interface TypeCast {

    /**
     * 类型转换
     *
     * @param methodBody the method body
     * @param varInst    the var inst
     * @param expectType the expect type
     * @return var inst
     */
    public VarInst cast(MethodBody methodBody, VarInst varInst, Type expectType);
}
