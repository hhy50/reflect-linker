package io.github.hhy50.linker.generate;


import io.github.hhy50.linker.token.Tokens;

/**
 * 参数依赖分析
 */
public class ArgsDepAnalysis {

    /**
     *
     */
    int[][] argsStack;

    public ArgsDepAnalysis() {
//        this.argsStack = ;
    }

    public void analyse(Tokens exprTokens) {
        int size = exprTokens.size();
        this.argsStack = new int[size][];

    }
}
