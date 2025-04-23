package io.github.hhy50.linker.test.nest.case1;

import io.github.hhy50.linker.annotations.Field;

/**
 * <p>ObjVisitor interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public interface LA_Runtime {
    /**
     * <p>getA.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a")
    Object get_a();

    /**
     * <p>setA.</p>
     *
     * @param a a {@link java.lang.Object} object.
     */
    @Field.Setter("a")
    void set_a(Object a);

    /**
     * <p>getB.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a.b")
    Object get_a_b();

    /**
     * <p>setB.</p>
     *
     * @param b a {@link java.lang.Object} object.
     */
    @Field.Setter("a.b")
    void set_a_b(Object b);

    /**
     * <p>getC.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a.b.c")
    Object get_a_b_c();

    /**
     * <p>setC.</p>
     *
     * @param c a {@link java.lang.Object} object.
     */
    @Field.Setter("a.b.c")
    void set_a_b_c(Object c);

    /**
     * <p>getStr.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("a.b.c.str")
    String get_a_b_c_str();

    /**
     * <p>setStr.</p>
     *
     * @param c a {@link java.lang.Object} object.
     */
    @Field.Setter("a.b.c.str")
    void set_a_b_c_str(Object c);

    /**
     * <p>getC2.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a.c")
    Object get_a_c();

    /**
     * <p>setC2.</p>
     *
     * @param c a {@link java.lang.Object} object.
     */
    @Field.Setter("a.c")
    void set_a_c(Object c);

    /**
     * <p>getStr2.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("a.c.str")
    String get_a_c_str();

    /**
     * <p>setStr2.</p>
     *
     * @param str2 a {@link java.lang.String} object.
     */
    @Field.Setter("a.c.str")
    void set_a_c_str(String str2);

    /**
     * <p>getD.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("a.d")
    Object get_a_d();

    /**
     * <p>setD.</p>
     *
     * @param d a int.
     */
    @Field.Setter("a.d")
    void set_a_d(int d);
}
