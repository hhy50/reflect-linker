package io.github.hhy.linker.test.nest.case1;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.annotations.Typed;

/**
 * <p>ObjVisitor2 interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
@Typed(name = "a", type = "io.github.hhy.linker.test.nest.case1.A2")
@Target.Bind("io.github.hhy.linker.test.nest.case1.Obj")
public interface ObjVisitor2 {
    /**
     * <p>getA.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a")
    Object getA();

    /**
     * <p>setA.</p>
     *
     * @param a a {@link java.lang.Object} object.
     */
    @Field.Setter("a")
    void setA(Object a);

    /**
     * <p>getB.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a.b")
    Object getB();

    /**
     * <p>setB.</p>
     *
     * @param b a {@link java.lang.Object} object.
     */
    @Field.Setter("a.b")
    void setB(Object b);

    /**
     * <p>getC.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a.b.c")
    Object getC();

    /**
     * <p>setC.</p>
     *
     * @param c a {@link java.lang.Object} object.
     */
    @Field.Setter("a.b.c")
    void setC(Object c);

    /**
     * <p>getC2.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a.c")
    Object getC2();

    /**
     * <p>setC2.</p>
     *
     * @param c a {@link java.lang.Object} object.
     */
    @Field.Setter("a.c")
    void setC2(Object c);

    /**
     * <p>getStr.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("a.b.c.str")
    String getStr();

    /**
     * <p>setStr.</p>
     *
     * @param c a {@link java.lang.Object} object.
     */
    @Field.Setter("a.b.c.str")
    void setStr(Object c);

    /**
     * <p>getStr2.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("a.c.str")
    String getStr2();

    /**
     * <p>setStr2.</p>
     *
     * @param str2 a {@link java.lang.String} object.
     */
    @Field.Setter("a.c.str")
    void setStr2(String str2);

    /**
     * <p>getD.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a.d")
    Object getD();

    /**
     * <p>setD.</p>
     *
     * @param d a int.
     */
    @Field.Setter("a.d")
    void setD(int d);
}
