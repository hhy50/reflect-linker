package io.github.hhy50.linker.test.typed;

public class User {

    private Object user2 = new User2();

    private User2[] users = {null, new User2()};

    public String getString(String str) {
        return "string";
    }
}
