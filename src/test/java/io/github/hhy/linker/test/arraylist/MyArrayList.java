package io.github.hhy.linker.test.arraylist;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Runtime;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.test.MyInteger;

/**
 * <p>MyArrayList interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
@Runtime
@Target.Bind("java.util.ArrayList")
public interface MyArrayList {

    /**
     * <p>add.</p>
     *
     * @param o a {@link java.lang.Object} object.
     */
    void add(Object o);
    /**
     * <p>clear.</p>
     */
    void clear();
    /**
     * <p>get.</p>
     *
     * @param i a int.
     */
    void get(int i);

    /**
     * <p>size.</p>
     *
     * @return a {@link io.github.hhy.linker.test.MyInteger} object.
     */
    MyInteger size();

    /**
     * <p>getElementData.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    @Field.Getter("elementData")
    Object getElementData();

    /**
     * <p>setElementData.</p>
     *
     * @param elementData a {@link java.lang.Object} object.
     */
    @Field.Setter("elementData")
    void setElementData(Object elementData);

    /**
     * <p>modCount.</p>
     *
     * @return a {@link io.github.hhy.linker.test.MyInteger} object.
     */
    @Field.Getter("modCount")
    MyInteger modCount();
}
