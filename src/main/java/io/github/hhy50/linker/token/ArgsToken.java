package io.github.hhy50.linker.token;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The type Args token.
 */
public class ArgsToken extends ArrayList<Token> {

    /**
     * The constant ALL.
     */
    public static final ArgsToken ALL = of(new AllPlaceholderToken());

    /**
     * Of args token.
     *
     * @param tokens the tokens
     * @return the args token
     */
    public static ArgsToken of(Token... tokens) {
        ArgsToken t = new ArgsToken();
        t.addAll(Arrays.asList(tokens));
        return t;
    }

    /**
     * Of all args token.
     *
     * @return the args token
     */
    public static ArgsToken ofAll() {
        return ALL;
    }

    @Override
    public String toString() {
        return "(" + this.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
    }

    /**
     * Is placeholder all boolean.
     *
     * @return the boolean
     */
    public boolean isPlaceholderAll() {
        return this.size() == 1 && this.get(0).kind() == Token.Kind.PlaceholderAll;
    }
}
