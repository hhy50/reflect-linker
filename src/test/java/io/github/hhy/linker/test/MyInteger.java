package io.github.hhy.linker.test;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.annotations.Typed;


//@Target.Bind("java.lang.Object")
/**
 * <p>MyInteger interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
@Target.Bind("java.lang.Integer")
public interface MyInteger {

    /**
     * <p>getValue.</p>
     *
     * @return a int.
     */
    @Typed(name = "target", type = "java.lang.Integer")
    @Field.Getter("value")
    int getValue();
}
