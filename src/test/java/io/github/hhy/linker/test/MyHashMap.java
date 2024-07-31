package io.github.hhy.linker.test;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;


@Target.Bind("java.util.HashMap")
public interface MyHashMap {

    void add(Object o);

    @Field.Getter("elementData")
    Object[] getElementData();

    @Field.Setter("elementData")
    void setElementData(Object[] elementData);

    @Field.Getter("modCount")
    int modCount();

    @Field.Setter("modCount")
    void modCount(int modCount);
}
