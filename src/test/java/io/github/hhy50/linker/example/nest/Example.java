package io.github.hhy50.linker.example.nest;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.exceptions.LinkerException;

class User {
    private String name;
    private int age;
}

class UserWrap {
    private User user;
}


interface UserVisitor {
    @Field.Getter("user")
    User getUser();

    @Field.Setter("user")
    void setUser(User user);

    @Field.Setter("user.name")
    void setName(String val);

    @Field.Getter("user.name")
    String getName();

    @Field.Setter("user.age")
    void setAge(int val);

    @Field.Getter("user.age")
    int getAge();
}

class Example {
    public static void main(String[] args) throws LinkerException {
        UserWrap userWrap = new UserWrap();
        UserVisitor userVisitor = LinkerFactory.createLinker(UserVisitor.class, userWrap);
        userVisitor.setUser(new User());
        userVisitor.setAge(20);
        userVisitor.setName("example");

        // do something
    }
}