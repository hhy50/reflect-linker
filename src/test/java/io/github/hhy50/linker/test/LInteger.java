package io.github.hhy50.linker.test;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.annotations.Typed;
import io.github.hhy50.linker.generate.builtin.SetTargetProvider;
import io.github.hhy50.linker.generate.builtin.TargetProvider;


/**
 * <p>MyInteger interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
//@Target.Bind("java.lang.Integer")
@Runtime
public interface LInteger extends TargetProvider<Integer>, SetTargetProvider {

    /**
     * <p>getValue.</p>
     *
     * @return a int.
     */
    @Typed(name = "target", value = "java.lang.Integer")
    @Field.Getter("value")
    int getValue();
}
