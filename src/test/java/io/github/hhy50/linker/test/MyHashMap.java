package io.github.hhy50.linker.test;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Target;


/**
 * <p>MyHashMap interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
@Target.Bind("java.util.HashMap")
public interface MyHashMap {

    /**
     * <p>add.</p>
     *
     * @param o a {@link java.lang.Object} object.
     */
    void add(Object o);

    /**
     * <p>getElementData.</p>
     *
     * @return an array of {@link java.lang.Object} objects.
     */
    @Field.Getter("elementData")
    Object[] getElementData();

    /**
     * <p>setElementData.</p>
     *
     * @param elementData an array of {@link java.lang.Object} objects.
     */
    @Field.Setter("elementData")
    void setElementData(Object[] elementData);

    /**
     * <p>modCount.</p>
     *
     * @return a int.
     */
    @Field.Getter("modCount")
    int modCount();

    /**
     * <p>modCount.</p>
     *
     * @param modCount a int.
     */
    @Field.Setter("modCount")
    void modCount(int modCount);
}
