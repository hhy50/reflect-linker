package io.github.hhy.linker.test;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;


@Target.Bind("java.util.ArrayList")
public interface MyArrayList {

    void add(Object o);

    @Field.Getter("elementData[0]")
    Object getElementData0();

    @Field.Getter("elementData.length")
    int len();

    @Field.Getter("elementData")
    Object[] getElementData();

    @Field.Setter("elementData")
    void setElementData(Object[] elementData);

    @Field.Getter("modCount")
    int modCount();

    @Field.Setter("modCount")
    void modCount(int modCount);
}
