package io.github.hhy50.linker.test.nest.case2;

import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.test.LInteger;


/**
 * <p>LMyObject interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
@Autolink
//@Runtime
interface Case2_Linker {

    /**
     * <p>getUser.</p>
     *
     * @return a {@link LUser} object.
     */
//    @Autolink
    @Field.Getter("user")
    @Autolink
    LUser getUser();

    /**
     * <p>setUser.</p>
     *
     * @param user a {@link LUser} object.
     */
    @Autolink
    @Field.Setter("user")
    void setUser(LUser user);

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
     * @return a {@link LInteger} object.
     */
    @Autolink
    @Field.Getter("user.age")
    LInteger getAge();

    /**
     * <p>setAge.</p>
     *
     * @param age a {@link LInteger} object.
     */
    @Autolink
    @Field.Setter("user.age")
    void setAge(LInteger age);

    @Autolink
    @Field.Setter("user.age")
    void setAge2(int age);

    @Autolink
    @Field.Setter("user.age")
    void setAge3(int age);

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
    @Method.Expr("boot().findModule('jdk.compiler').get()")
//    @Method.Expr("user.getName(a.b.c.d, a.b.c.d, a.b.c, a.b.c.d.getAA('a'), 1).a.b.c.d.get()")
    String getSuperName();

    @Method.InvokeSuper
    @Method.Expr("user.a.b.c.d.e.f.e.a.a.a.a")
    String getA();

    // runtime
    /**
     * <p>getName2.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Method.Expr("user.getName2()")
    String getName2();
    /**
     * <p>getName3.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Method.Expr("user.getName3()")
    String getName3();
    /**
     * <p>getName4.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Method.Expr("user.getName4()")
    String getName4();

//    @Typed(name = "user", type = "io.github.hhy50.linker.test.nest.case2.UserVo")
    /**
     * <p>superToString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Method.InvokeSuper
    @Method.Expr("user.toString()")
    String superToString();
}
