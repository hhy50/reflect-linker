package io.github.hhy50.linker.generate;


import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.token.ArgsToken;
import io.github.hhy50.linker.token.PlaceholderToken;
import io.github.hhy50.linker.token.Token;
import org.objectweb.asm.Type;

/**
 * 参数依赖分析
 */
public class ArgsDepAnalysis {

    /**
     *
     */
    int[] argsStack = new int[0];
    Type[] argsType = new Type[0];
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
                Type argType = argumentTypes[i];
//                if (runtime) {
//                    argType = ObjectVar.TYPE;
//                }
                if (argsStack.length < index+1) {
                    int[] newStackArr = new int[index + 1];
                    Type[] newTypesArr = new Type[index + 1];
                    System.arraycopy(argsStack, 0, newStackArr, 0, argsStack.length);
                    System.arraycopy(argsType, 0, newTypesArr, 0, argsType.length);
                    argsStack = newStackArr;
                    argsType = newTypesArr;
                }
                argsStack[index] += 1;
                if (argsType[index] != null && argsType[index] != argType) {
                    argsType[index] = ObjectVar.TYPE;
                } else {
                    argsType[index] = argType;
                }
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
}