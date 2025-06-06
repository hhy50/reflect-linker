package io.github.hhy50.linker.test.accessor;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;

public class User extends UserParent  {
    String name;
    int age;

    void test1() {
        System.out.println(12345);
    }

    static void test2() {
        System.out.println(54321);
    }

    public String toString() {
        return "User.toString()";
    }
}


class UserParent {
    public String toString() {
        return "UserParent.toString()";
    }
}

class UserAccessor {
    static  LUser lSUser;

    static {
        try {
            lSUser = LinkerFactory.createStaticLinker(LUser.class, User.class);
        } catch (LinkerException e) {
            throw new RuntimeException(e);
        }
    }

    LUser lUser;

    public UserAccessor(User user) throws LinkerException {
        lUser = LinkerFactory.createLinker(LUser.class, user);
    }

    public void setName(LString name) {
        lUser.setName(name);
    }

    public String getName() {
        return lUser.getName();
    }

    public void setAge(int age) {
        lUser.setAge(age);
    }

    public int getAge() {
        return lUser.getAge();
    }

    public void test1() {
        lUser.test1();
    }

    public static void test2() {
        lSUser.test2();
    }

    public LString self_toString() {
        return lUser.toString_1();
    }

    public String super_toString() {
        return lUser.toString_2();
    }

    public String super_super_toString() {
        return lUser.toString_3();
    }
}

@Autolink
interface LUser {
    @Method.Constructor
    LUser newInstance();

    @Field.Setter("name")
    void setName(LString name);

    @Field.Getter("name")
    String getName();

    @Field.Setter("age")
    void setAge(int age);

    @Field.Getter("age")
    int getAge();

    @Method.Name("test1")
    void test1();

    @Method.Name("test2")
    void test2();

    @Method.Name("toString")
    LString toString_1();

    @Method.InvokeSuper
    @Method.Name("toString")
    String toString_2();

    @Method.InvokeSuper("java.lang.Object")
    @Method.Name("toString")
    String toString_3();
}


interface LString {

}