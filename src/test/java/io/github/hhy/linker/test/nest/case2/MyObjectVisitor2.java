package io.github.hhy.linker.test.nest.case2;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.annotations.Typed;


/**
 * <p>MyObjectVisitor2 interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
@Typed(name = "user", type = "io.github.hhy.linker.test.nest.case2.UserVo")
@Target.Bind("io.github.hhy.linker.test.nest.case2.MyObject")
public interface MyObjectVisitor2 {

    /**
     * <p>getUser.</p>
     *
     * @return a {@link io.github.hhy.linker.test.nest.case2.User} object.
     */
    @Field.Getter("user")
    User getUser();

    /**
     * <p>setUser.</p>
     *
     * @param user a {@link io.github.hhy.linker.test.nest.case2.User} object.
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
