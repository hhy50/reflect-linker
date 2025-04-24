package io.github.hhy50.linker.test.statictest;

import io.github.hhy50.linker.annotations.Field;

/**
 * <p>MyStaticClass interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
interface LStaticFieldClass {

    /**
     * <p>getA.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("aaa")
    public String getA();

    /**
     * <p>getA2.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("aaa2")
    public String getA2();

    /**
     * <p>getObjAaa.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("obj2.aaa")
    public String getObjAaa();

    /**
     * <p>getObjAaa2.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("obj2.obj3.aaa")
    public String getObjAaa2();

    /**
     * <p>getObj2Aaa2.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Field.Getter("obj3.obj3.aaa")
    public String getObj2Aaa2();

    /**
     * <p>setA.</p>
     *
     * @param aaa a {@link java.lang.Object} object.
     */
    @Field.Setter("aaa")
    public void setA(Object aaa);
}
