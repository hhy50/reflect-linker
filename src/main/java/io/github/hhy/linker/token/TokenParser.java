package io.github.hhy.linker.token;



import java.util.Iterator;

public class TokenParser {

    public Tokens parse(String tokensStr) {
        if (tokensStr == null || (tokensStr = tokensStr.trim()).length() == 0) {
            return null;
        }

        Tokens tokens = new Tokens();
        Parser parser = new Parser(tokensStr);
        while (parser.hasNext()) {
            Token next = parser.next();
            tokens.add(next);
        }
        return tokens;
    }

    static class Parser implements Iterator<Token> {
        public static final char SPLIT = '.';

        // 索引访问符
        public static final char INDEX_ACCESS_START_SYMBOL = '[';
        public static final char INDEX_ACCESS_END_SYMBOL = ']';

        private String tokenStr;
        private char[] tokenSymbols;

        private int pos;

        public Parser(String tokenStr) {
            this.tokenStr = tokenStr;
            this.tokenSymbols = tokenStr.toCharArray();
        }

        @Override
        public boolean hasNext() {
            return pos < tokenSymbols.length;
        }

        @Override
        public Token next() {
            String owner = null;
            String index = null;
            boolean mapIndex = false;
            for (int i = pos; i < tokenSymbols.length; i++) {
                if ((tokenSymbols[i] >= 'a' && tokenSymbols[i] <= 'z')
                        || (tokenSymbols[i] >= 'A' && tokenSymbols[i] <= 'Z')
                        || (tokenSymbols[i] >= '0' && tokenSymbols[i] <= '9')) {
                    continue;
                } else if (tokenSymbols[i] == INDEX_ACCESS_START_SYMBOL) {
                    owner = tokenStr.substring(pos, i);
                    pos = i;
                } else if (tokenSymbols[i] == INDEX_ACCESS_END_SYMBOL) {
                    int end = i;
                    if (mapIndex) {
                        pos++;
                        end--;
                    }
                    index = tokenStr.substring(pos+1, end);
                    pos = i+1;
                } else if (tokenSymbols[i] == SPLIT) {
                    if (owner == null) {
                        owner = tokenStr.substring(pos, i);
                    }
                    pos = i+1;
                    break;
                } else if (tokenSymbols[i] == '\'') {
                    mapIndex = true;
                } else {
                    throw new ParseException("Unknown symbol "+tokenSymbols[i]);
                }
            }
            if (owner == null) {
                owner = tokenStr;
                pos = tokenSymbols.length;
            }
            if (mapIndex && index == null) {
                throw new ParseException("Unknown symbol '");
            }
            if (index != null) {
                return mapIndex ? new MapKeyToken(owner, index) : new ArrayIndexFieldToken(owner, index);
            }
            return new FieldToken(owner);
        }
    }
}
