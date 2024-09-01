package io.github.hhy.linker.test.nest.case2;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Method;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.test.MyInteger;


@Target.Bind("io.github.hhy.linker.test.nest.case2.MyObject")
public interface MyObjectVisitor {

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
    void setAge(MyInteger age);

    @Field.Getter("user.address")
    String getAddress();

    @Field.Setter("user.address")
    void setAddress(String address);

//    @Typed(name = "user", type = "io.github.hhy.linker.test.nest.case2.UserVo")
    @Method.InvokeSuper("io.github.hhy.linker.test.nest.case2.User")
    @Method.Name("user.getName")
    String getSuperName();

//    @Typed(name = "user", type = "io.github.hhy.linker.test.nest.case2.UserVo")
    @Method.InvokeSuper("java.lang.Object")
    @Method.Name("user.toString")
    String superToString();
}
