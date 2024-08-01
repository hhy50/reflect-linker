package io.github.hhy.linker.token;

public abstract class Token {

    public Token next;

    /**
     * 获取token值
     * @return
     */
    public abstract String value();
}
