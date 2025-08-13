package io.github.hhy50.linker.generate;


import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.token.ArgsToken;
import io.github.hhy50.linker.token.PlaceholderToken;
import io.github.hhy50.linker.token.Token;
import io.github.hhy50.linker.token.Tokens;
import org.objectweb.asm.Type;

import java.util.Arrays;

/**
 * 参数依赖分析
 */
public class ArgsDepAnalysis {

    private Class[] argsType;

    /**
     *
     */
    int[] argsStack;
    Type[] argsType2;
    Type rType;
    public ArgsDepAnalysis() {

    }

    public ArgsDepAnalysis(Class[] argsType) {
        this.argsType = argsType;
        this.argsStack = new int[argsType.length];
        this.argsType2 = new Type[argsType.length];
        Arrays.fill(this.argsType2, ObjectVar.TYPE);
    }

    public void analyse(Type methodType, ArgsToken argsToken) {
        Type[] argumentTypes = methodType.getArgumentTypes();
        this.rType = methodType.getReturnType();
        for (int i = 0; i < argumentTypes.length; i++) {
            Token arg = argsToken.get(i);
            if (arg instanceof PlaceholderToken) {
                int index = ((PlaceholderToken) arg).index;
                argsStack[index] += 1;
                argsType2[index] = argumentTypes[i];
            }
        }
    }

    public Type getReturnType() {
        return rType;
    }

    public Type[] getArgsType() {
        return argsType2;
    }

    public void analyse(Tokens exprTokens) {
        int size = exprTokens.size();
//        exprTokens
    }
}
