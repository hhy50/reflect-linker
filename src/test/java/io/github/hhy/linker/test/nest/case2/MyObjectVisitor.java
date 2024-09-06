package io.github.hhy.linker.test.nest.case2;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Method;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.test.MyInteger;


@Target.Bind("io.github.hhy.linker.test.nest.case2.MyObject")
public interface MyObjectVisitor {

    @Field.Getter("user")
    UserVisitor getUser();

    @Field.Setter("user")
    void setUser(UserVisitor user);

    @Field.Getter("user.name")
    String getName();

    @Field.Setter("user.name")
    void setName(String name);

    @Field.Getter("user.age")
    MyInteger getAge();

    @Field.Setter("user.age")
    void setAge(MyInteger age);

    @Field.Getter("user.address")
    String getAddress();

    @Field.Setter("user.address")
    void setAddress(String address);

//    @Typed(name = "user", type = "io.github.hhy.linker.test.nest.case2.UserVo")
//    @Runtime
    @Method.InvokeSuper
    @Method.Name("user.getName")
    String getSuperName();

    // runtime
    @Method.Name("user.getName2")
    String getName2();
    @Method.Name("user.getName3")
    String getName3();
    @Method.Name("user.getName4")
    String getName4();

//    @Typed(name = "user", type = "io.github.hhy.linker.test.nest.case2.UserVo")
    @Method.InvokeSuper
    @Method.Name("user.toString")
    String superToString();
}
