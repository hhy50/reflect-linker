package io.github.hhy50.linker.token;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The type Args token.
 */
public class ArgsToken extends ArrayList<Token> {

    public static ArgsToken of(Token... tokens) {
        ArgsToken t = new ArgsToken();
        t.addAll(Arrays.asList(tokens));
        return t;
    }

    @Override
    public String toString() {
        return "(" + this.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
    }
}
