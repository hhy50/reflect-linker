package io.github.hhy50.linker.test.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GenericType {
    static class User {
        private String name = UUID.randomUUID().toString();
        private String addr;
    }

    public List<User> users;

    public GenericType() {
        this.users = new ArrayList<>();
        this.users.add(new User());
        this.users.add(new User());
        this.users.add(new User());
        this.users.add(new User());
    }
}
