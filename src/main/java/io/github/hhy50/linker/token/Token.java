package io.github.hhy50.linker.token;

import java.util.List;

/**
 * The interface Token.
 */
public interface Token {

    enum Kind {
        Field,
        Method,
        Placeholder,
        PlaceholderAll,
        IntConst,
        StrConst,
        Tokens,
    }

    Kind kind();

    /**
     * Sets index.
     *
     * @param index the index
     */
    default void setIndex(List<ConstToken> index) {

    }

    default void setNullable(boolean nullable) {

    }


}
