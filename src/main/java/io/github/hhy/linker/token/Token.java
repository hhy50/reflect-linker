package io.github.hhy.linker.token;

public abstract class Token {

    public Token next;

    /**
     * 获取token值
     *
     * @return
     */
    public abstract String value();

    /**
     * 是否是array表达式
     * @return
     */
    public boolean arrayExpr() {
        return false;
    }

    /**
     * 是否是数组表达式
     * @return
     */
    public boolean mapExpr() {
        return false;
    }
}
