package io.github.hhy50.linker.token;


public interface ConstToken {

    class Int implements Token {
        private final String val;
        public Int(String val) {
            this.val = val;
        }
        @Override
        public String value() {
            return "";
        }
    }

    class Str implements Token {
        private final String val;
        public Str(String val) {
            this.val = val;
        }
        @Override
        public String value() {
            return "";
        }
    }
}
