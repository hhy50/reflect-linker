package io.github.hhy.linker.test.nest.case1;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;

/**
 * <p>AVisitor interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
@Target.Bind("io.github.hhy.linker.test.nest.case1.A")
public interface AVisitor {
    /**
     * <p>getA.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a")
    Object getA();

    /**
     * <p>getB.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a.b")
    Object getB();

    /**
     * <p>getC.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a.b.c")
    Object getC();

    /**
     * <p>getC2.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a.c")
    Object getC2();

    /**
     * <p>getStr.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("a.b.c.str")
    String getStr();
}
