package io.github.hhy50.linker.token;


/**
 * The interface Const token.
 */
public interface ConstToken extends Token {

    /**
     * The type Int.
     */
    class Int implements ConstToken {
        private final String val;

        /**
         * Instantiates a new Int.
         *
         * @param val the val
         */
        public Int(String val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return this.val;
        }

        @Override
        public String value() {
            return "";
        }
    }

    /**
     * The type Str.
     */
    class Str implements ConstToken {
        private final String val;

        /**
         * Instantiates a new Str.
         *
         * @param val the val
         */
        public Str(String val) {
            this.val = val;
        }

        @Override
        public String value() {
            return "";
        }

        @Override
        public String toString() {
            return "'"+val+"'";
        }
    }

    /**
     * Of int const token.
     *
     * @param val the val
     * @return the const token
     */
    static ConstToken ofInt(String val) {
        return new Int(val);
    }

    /**
     * Of str const token.
     *
     * @param val the val
     * @return the const token
     */
    static ConstToken ofStr(String val) {
        return new Str(val);
    }
}
