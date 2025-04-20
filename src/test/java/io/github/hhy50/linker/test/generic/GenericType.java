package io.github.hhy50.linker.test.generic;

import java.util.ArrayList;
import java.util.List;

public class GenericType {
    static class User {
        private String name;
        private String addr;
    }

    public GenericType() {
        this.users = new ArrayList<>();
        this.users.add(new User());
        this.users.add(new User());
        this.users.add(new User());
        this.users.add(new User());
    }


    public List<User> users;
}
