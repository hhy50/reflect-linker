package io.github.hhy50.linker.token;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The type Args token.
 */
public class ArgsToken extends ArrayList<Token> {

    public static final ArgsToken ALL = of(new AllPlaceholderToken());

    public static ArgsToken of(Token... tokens) {
        ArgsToken t = new ArgsToken();
        t.addAll(Arrays.asList(tokens));
        return t;
    }

    public static ArgsToken ofAll() {
        return ALL;
    }

    @Override
    public String toString() {
        return "(" + this.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
    }

    public boolean isPlaceholderAll() {
        return this.size() == 1 && this.get(0).kind() == Token.Kind.PlaceholderAll;
    }
}
