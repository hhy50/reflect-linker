package io.github.hhy50.linker.test.nest.case2;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Typed;


/**
 * <p>MyObjectVisitor2 interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
@Typed(name = "user", type = "io.github.hhy50.linker.test.nest.case2.UserVo")
public interface MyObjectVisitor2 {

    /**
     * <p>getUser.</p>
     *
     * @return a {@link User} object.
     */
    @Field.Getter("user")
    User getUser();

    /**
     * <p>setUser.</p>
     *
     * @param user a {@link User} object.
     */
    @Field.Setter("user")
    void setUser(User user);

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
     * @return a int.
     */
    @Field.Getter("user.age")
    int getAge();

    /**
     * <p>setAge.</p>
     *
     * @param age a int.
     */
    @Field.Setter("user.age")
    void setAge(int age);

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
}
