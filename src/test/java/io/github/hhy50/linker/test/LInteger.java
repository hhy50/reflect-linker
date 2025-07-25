package io.github.hhy50.linker.test;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Typed;
import io.github.hhy50.linker.generate.builtin.TargetProvider;


/**
 * <p>MyInteger interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
//@Target.Bind("java.lang.Integer")
public interface LInteger extends TargetProvider<Integer> {

    /**
     * <p>getValue.</p>
     *
     * @return a int.
     */
    @Typed(name = "target", value = "java.lang.Integer")
    @Field.Getter("value")
    int getValue();
}
