package io.github.hhy50.linker.token;


import io.github.hhy50.linker.define.ParseContext;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

/**
 * The interface Const token.
 */
public interface ConstToken extends Token {

    /**
     * The type Int.
     */
    class Int implements ConstToken, ArgType {
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
        public Object getValue() {
            return Integer.parseInt(val);
        }

        @Override
        public Type getType(ParseContext context, Method methodDefine) {
            return Type.INT_TYPE;
        }
    }

    /**
     * The type Str.
     */
    class Str implements ConstToken, ArgType {
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
        public String toString() {
            return "'"+val+"'";
        }

        @Override
        public Object getValue() {
            return val;
        }

        @Override
        public Type getType(ParseContext context, Method methodDefine) {
            return TypeUtil.STRING_TYPE;
        }
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public Object getValue();

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
