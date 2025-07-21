package io.github.hhy50.linker.token;


import io.github.hhy50.linker.exceptions.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Token parser.
 */
public class TokenParser {

    /**
     * The Owner flag.
     */
    static int OWNER_FLAG = 1;
    /**
     * The Arg flag.
     */
    static int ARG_FLAG = 1 << 1;

    /**
     * The Empty.
     */
    static Tokens EMPTY = new Tokens();

    /**
     * Parse tokens.
     *
     * @param tokensStr the tokens str
     * @return the tokens
     */
    public Tokens parse(String tokensStr) {
        if (tokensStr == null || (tokensStr = tokensStr.trim()).length() == 0) {
            return EMPTY;
        }

        Tokens tokens = new Tokens();
        ParserIter parser = new ParserIter(tokensStr);
        while (parser.hasNext()) {
            Token next = parser.parseNextToken();
            tokens.add(next);
        }
        return tokens;
    }

    /**
     * The interface Token item.
     */
    interface TokenItem {
        /**
         * Gets token.
         *
         * @return the token
         */
        Token getToken();
    }

    /**
     * The type Parser iter.
     */
    static class ParserIter {
        /**
         * The constant SPLIT.
         */
        public static final char SPLIT = '.';

        /**
         * The constant INDEX_ACCESS_START_SYMBOL.
         */
        public static final char INDEX_ACCESS_START_SYMBOL = '[';
        /**
         * The constant INDEX_ACCESS_END_SYMBOL.
         */
        public static final char INDEX_ACCESS_END_SYMBOL = ']';

        /**
         * The constant METHOD_START_SYMBOL.
         */
        public static final char METHOD_START_SYMBOL = '(';
        /**
         * The constant METHOD_END_SYMBOL.
         */
        public static final char METHOD_END_SYMBOL = ')';

        private String tokenStr;
        private char[] tokenSymbols;

        private int pos;

        /**
         * Instantiates a new Parser iter.
         *
         * @param tokenStr the token str
         */
        public ParserIter(String tokenStr) {
            this.tokenStr = tokenStr;
            this.tokenSymbols = tokenStr.toCharArray();
        }

        /**
         * Has next boolean.
         *
         * @return the boolean
         */
        public boolean hasNext() {
            return pos < tokenSymbols.length;
        }

        /**
         * Parse next token token.
         *
         * @return the token
         */
        public Token parseNextToken() {
            if (pos >= tokenSymbols.length) {
                return null;
            }
            if (tokenSymbols[pos] == SPLIT) {
                pos++;
            }
            String identifier = nextIdentifier(OWNER_FLAG);
            Token owner = null;
            if (tokenSymbols[pos] == METHOD_START_SYMBOL) {
                owner = parseMethodToken(identifier);
            } else {
                owner = new FieldToken(identifier);
            }
            if (tokenSymbols[pos] == INDEX_ACCESS_START_SYMBOL) {
                owner.setIndex(parseIndexToken());
            }
            return owner;
        }

        private String nextIdentifier(int flag) {
            String identifier = null;
            for (int i = pos; i <= tokenSymbols.length; i++) {
                if (isIdentifierCharacter(i)) {
                    continue;
                }
                char c = tokenSymbols[i];
                if (c != SPLIT && c != INDEX_ACCESS_START_SYMBOL && c != METHOD_START_SYMBOL
                        && ((flag & ARG_FLAG) > 0 && c != ',') // 如果解析的是参数，那么 `,` 可以作为结束符号
                ) {
                    throwParseException("Unknown symbol "+tokenSymbols[i], i);
                }

                identifier = tokenStr.substring(pos, i);
                if ((flag & OWNER_FLAG) > 0) {
                    // 如果owner的第一个字符不是a-z, A-Z, _
                    if (!Character.isJavaIdentifierStart(tokenSymbols[pos])) {
                        throwParseException("Unknown symbol "+tokenSymbols[pos], pos);
                    }
                }
                pos = tokenSymbols[i] == SPLIT ? i+1 : i;
                break;
            }
            return identifier;
        }

        /**
         * Is identifier character boolean.
         *
         * @param i the
         * @return the boolean
         */
        public boolean isIdentifierCharacter(int i) {
            return Character.isJavaIdentifierPart(tokenSymbols[i]);
        }

        /**
         * Parse string const token.
         *
         * @return the const token
         */
        public ConstToken parseString() {
            pos++; // 跳过开头的单引号
            int start = pos;
            while (pos < tokenSymbols.length) {
                if (tokenSymbols[pos] == '\'') {
                    String value = tokenStr.substring(start, pos);
                    pos++; // 跳过结尾的单引号
                    return ConstToken.ofStr(value);
                }
                pos++;
            }
            throwParseException("Unclosed string", start-1);
            return null;
        }

        /**
         * Parse int const token.
         *
         * @return the const token
         */
        public ConstToken parseInt() {
            int start = pos;
            while (pos < tokenSymbols.length) {
                if (isIdentifierCharacter(pos)) {
                    pos++;
                } else {
                    break;
                }
            }
            String val = tokenStr.substring(start, pos);
            Integer.parseInt(val); // check
            return ConstToken.ofInt(val);
        }

        /**
         * Parse index token index token.
         *
         * @return the index token
         */
        public IndexToken parseIndexToken() {
            if (tokenSymbols[pos] != INDEX_ACCESS_START_SYMBOL) {
                return null;
            }
            List<ConstToken> index = new ArrayList<>();
            while (tokenSymbols[pos] == INDEX_ACCESS_START_SYMBOL) {
                pos++; // 跳过 [
                if (tokenSymbols[pos] == '\'') {
                    index.add(parseString());
                } else {
                    index.add(parseInt());
                }
                if (tokenSymbols[pos] != INDEX_ACCESS_END_SYMBOL) {
                    throwParseException("Unclosed index", pos);
                }
                pos++; // 跳过 ]
            }
            assert index.size() > 0;
            return new IndexToken(index);
        }

        /**
         * Parse method token method token.
         *
         * @param methodName the method name
         * @return the method token
         */
        public MethodToken parseMethodToken(String methodName) {
            if (tokenSymbols[pos] != METHOD_START_SYMBOL) {
                throwParseException("Unknown symbol "+tokenSymbols[pos], pos);
            }
            pos++; // 跳过 (

            List<Token> args = new ArrayList<>();
            while (pos < tokenSymbols.length) {
                if (tokenSymbols[pos] != METHOD_END_SYMBOL) {
                    String identifier = nextIdentifier(ARG_FLAG);
                    args.add(parseNextToken());
                }
            }
            return null;
        }

        /**
         * Throw parse exception.
         *
         * @param message the message
         * @param pos     the pos
         */
        public void throwParseException(String message, int pos) {
            throw new ParseException(String.format("At position %d %s", pos+1, message));
        }
    }
}
