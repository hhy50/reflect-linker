package io.github.hhy.linker.token;


public class MapKeyToken extends FieldToken {

    public String key;

    public MapKeyToken(String fieldName, String key) {
        super(fieldName);
        this.key = key;
    }

    @Override
    public String toString() {
        return fieldName+"['"+key+"']";
    }
}
