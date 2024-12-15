package io.github.hhy50.linker.test.nest.case2;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.test.MyInteger;


/**
 * <p>MyObjectVisitor interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public interface MyObjectVisitor {

    /**
     * <p>getUser.</p>
     *
     * @return a {@link UserVisitor} object.
     */
    @Field.Getter("user")
    UserVisitor getUser();

    /**
     * <p>setUser.</p>
     *
     * @param user a {@link UserVisitor} object.
     */
    @Field.Setter("user")
    void setUser(UserVisitor user);

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("user.name")
    String getName();

    /**
     * <p>setName.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    @Field.Setter("user.name")
    void setName(String name);

    /**
     * <p>getAge.</p>
     *
     * @return a {@link MyInteger} object.
     */
    @Field.Getter("user.age")
    MyInteger getAge();

    /**
     * <p>setAge.</p>
     *
     * @param age a {@link MyInteger} object.
     */
    @Field.Setter("user.age")
    void setAge(MyInteger age);

    /**
     * <p>getAddress.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("user.address")
    String getAddress();

    /**
     * <p>setAddress.</p>
     *
     * @param address a {@link java.lang.String} object.
     */
    @Field.Setter("user.address")
    void setAddress(String address);

//    @Typed(name = "user", type = "io.github.hhy50.linker.test.nest.case2.UserVo")
//    @Runtime
    /**
     * <p>getSuperName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Method.InvokeSuper
    @Method.Name("user.getName")
    String getSuperName();

    // runtime
    /**
     * <p>getName2.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Method.Name("user.getName2")
    String getName2();
    /**
     * <p>getName3.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Method.Name("user.getName3")
    String getName3();
    /**
     * <p>getName4.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Method.Name("user.getName4")
    String getName4();

//    @Typed(name = "user", type = "io.github.hhy50.linker.test.nest.case2.UserVo")
    /**
     * <p>superToString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Method.InvokeSuper
    @Method.Name("user.toString")
    String superToString();
}
