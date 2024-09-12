package io.github.hhy.linker.example.nest;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.exceptions.LinkerException;

class User {
    private String name;
    private int age;
}

class UserWrap {
    private User user;
}


@Target.Bind("io.github.hhy.linker.example.nest.UserWrap")
interface UserVisitor {
    /**
     * <p>getUser.</p>
     *
     * @return a {@link io.github.hhy.linker.example.nest.User} object.
     */
    @Field.Getter("user")
    User getUser();

    /**
     * <p>setUser.</p>
     *
     * @param user a {@link io.github.hhy.linker.example.nest.User} object.
     */
    @Field.Setter("user")
    void setUser(User user);

    /**
     * <p>setName.</p>
     *
     * @param val a {@link java.lang.String} object.
     /**
      * <p>main.</p>
      *
      * @param args an array of {@link java.lang.String} objects.
      * @throws io.github.hhy.linker.exceptions.LinkerException if any.
      */
     */
    @Field.Setter("user.name")
    void setName(String val);

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("user.name")
    String getName();

    /**
     * <p>setAge.</p>
     *
     * @param val a int.
     */
    @Field.Setter("user.age")
    void setAge(int val);

    /**
     * <p>getAge.</p>
     *
     * @return a int.
     */
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
