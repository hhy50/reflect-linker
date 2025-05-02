package io.github.hhy50.linker.test.nest.case2;


import io.github.hhy50.linker.annotations.Field;

/**
 * <p>UserVisitor interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */

//@Target.Bind("io.github.hhy50.linker.test.nest.case2.User")
public interface LUser {
    @Field.Getter("name")
    String getName();

    @Field.Getter("name")
    Integer getAge();
}
