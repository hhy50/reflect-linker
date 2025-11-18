package io.github.hhy50.linker.token;


import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * The type Args token.
 */
public class ArgsToken extends ArrayList<Token> {


    @Override
    public String toString() {
        return "(" + this.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
    }
}
