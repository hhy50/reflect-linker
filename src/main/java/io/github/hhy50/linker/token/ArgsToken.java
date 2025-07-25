package io.github.hhy50.linker.token;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Args token.
 */
public class ArgsToken implements Token {
    /**
     * The Args.
     */
    public List<Token> args = new ArrayList<>();

    /**
     * Instantiates a new Args token.
     */
    public ArgsToken() {

    }

    /**
     * Add.
     *
     * @param token the token
     */
    public void add(Token token) {
        this.args.add(token);
    }

    @Override
    public String toString() {
        return "(" + args.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
    }
}
