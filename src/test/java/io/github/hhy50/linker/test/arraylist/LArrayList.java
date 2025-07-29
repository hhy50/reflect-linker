package io.github.hhy50.linker.test.arraylist;

import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.generate.builtin.TargetProvider;
import io.github.hhy50.linker.test.LInteger;

import java.util.List;

/**
 * <p>MyArrayList interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
//@Runtime
public interface LArrayList extends TargetProvider<List> {

    @Method.Constructor
    LArrayList newList();

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
    Object get(int i);

    /**
     * <p>size.</p>
     *
     * @return a {@link LInteger} object.
     */
    @Autolink
    LInteger size();

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

    @Field.Getter("elementData[0]")
    Object get0();
    @Field.Getter("elementData[1]")
    Object get1();
    @Field.Getter("elementData[2]")
    Object get2();
    @Field.Getter("elementData[3]")
    Object get3();
    @Field.Getter("elementData[4]")
    Object get4();
    @Field.Getter("elementData[5]")
    Object get5();

    /**
     * <p>modCount.</p>
     *
     * @return a {@link LInteger} object.
     */
    @Autolink
    @Field.Getter("modCount")
    LInteger modCount();

    @Autolink
    @Field.Setter("modCount")
    void setModCount(LInteger i);
}
