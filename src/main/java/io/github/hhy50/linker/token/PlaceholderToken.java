package io.github.hhy50.linker.token;

public class PlaceholderToken implements Token{

    public int index;
    public PlaceholderToken(int index) {
        this.index = index;
    }

    @Override
    public String value() {
        return "";
    }
}
