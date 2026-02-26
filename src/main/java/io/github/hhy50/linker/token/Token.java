package io.github.hhy50.linker.token;

import java.util.List;

/**
 * The interface Token.
 */
public interface Token {

    /**
     * The enum Kind.
     */
    enum Kind {
        /**
         * Field kind.
         */
        Field,
        /**
         * Method kind.
         */
        Method,
        /**
         * Placeholder kind.
         */
        Placeholder,
        /**
         * Placeholder all kind.
         */
        PlaceholderAll,
        /**
         * Int const kind.
         */
        IntConst,
        /**
         * Str const kind.
         */
        StrConst,
        /**
         * Tokens kind.
         */
        Tokens,
    }

    /**
     * Kind kind.
     *
     * @return the kind
     */
    Kind kind();

    /**
     * Sets index.
     *
     * @param index the index
     */
    default void setIndex(List<ConstToken> index) {

    }

    /**
     * Sets nullable.
     *
     * @param nullable the nullable
     */
    default void setNullable(boolean nullable) {

    }


}
