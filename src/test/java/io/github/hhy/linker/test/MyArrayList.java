package io.github.hhy.linker.test;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;


@Target.Bind("java.util.ArrayList")
public interface MyArrayList {

    @Field.Getter("elementData")
    Object[] getElementData();

    //    void add(Object o);
//
    @Field.Setter("elementData")
    void setElementData(Object[] elementData);
//
//    @Field.Getter("modCount")
//    int modCount();
//
//    @Field.Setter("modCount")
//    void modCount(int modCount);
}
