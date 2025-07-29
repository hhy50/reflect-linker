package io.github.hhy50.linker.token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Index token.
 */
public class IndexToken implements Token {

    /**
     * The Index.
     */
    public List<ConstToken> values;

    /**
     * Instantiates a new Index token.
     *
     * @param index the index
     */
    public IndexToken(List<ConstToken> index) {
        this.values = index;
    }

    @Override
    public String toString() {
        String collect = values.stream().map(Object::toString).collect(Collectors.joining("]["));
        return "["+collect+"]";
    }

    public List<Object> toValues() {
        return values.stream().map(ConstToken::getValue).collect(Collectors.toList());
    }
}
