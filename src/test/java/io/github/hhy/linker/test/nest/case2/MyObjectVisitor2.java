package io.github.hhy.linker.test.nest.case2;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.annotations.Typed;


@Typed(name = "user", type = "io.github.hhy.linker.test.nest.case2.UserVo")
@Target.Bind("io.github.hhy.linker.test.nest.case2.MyObject")
public interface MyObjectVisitor2 {

    @Field.Getter("user")
    User getUser();

    @Field.Setter("user")
    void setUser(User user);

    @Field.Getter("user.name")
    String getName();

    @Field.Setter("user.name")
    void setName(String name);

    @Field.Getter("user.age")
    int getAge();

    @Field.Setter("user.age")
    void setAge(int age);

    @Field.Getter("user.address")
    String getAddress();

    @Field.Setter("user.address")
    void setAddress(String address);
}
