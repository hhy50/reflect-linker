package io.github.hhy50.linker.generate;


import io.github.hhy50.linker.token.ArgsToken;
import io.github.hhy50.linker.token.PlaceholderToken;
import io.github.hhy50.linker.token.Token;
import io.github.hhy50.linker.token.Tokens;
import org.objectweb.asm.Type;

/**
 * 参数依赖分析
 */
public class ArgsDepAnalysis {

    /**
     *
     */
    int[] argsStack;
    Type[] argsType;
    Type rType = Type.VOID_TYPE;

    public ArgsDepAnalysis() {

    }

    public void analyse(Type methodType, ArgsToken argsToken) {
        Type[] argumentTypes = methodType.getArgumentTypes();
        this.rType = methodType.getReturnType();
        for (int i = 0; i < argumentTypes.length; i++) {
            Token arg = argsToken.get(i);
            if (arg instanceof PlaceholderToken) {
                int index = ((PlaceholderToken) arg).index;
                argsStack[index] += 1;
                argsType[index] = argumentTypes[i];
            }
        }
    }

    public Type getReturnType() {
        return rType;
    }

    public Type[] getArgsType() {
        if (argsType == null || argsType.length == 0) {
            return new Type[0];
        }
        return argsType;
    }

    public void analyse(Tokens exprTokens) {
        int size = exprTokens.size();
//        exprTokens
    }
}
