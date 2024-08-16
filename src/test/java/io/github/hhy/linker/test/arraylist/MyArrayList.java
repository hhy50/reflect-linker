package io.github.hhy.linker.test.arraylist;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.test.MyInteger;


@Target.Bind("java.util.ArrayList")
public interface MyArrayList {

    void add(Object o);

//    @Field.Getter("elementData.length")
//    int cap();

    @Field.Getter("elementData")
    Object[] getElementData();

    @Field.Setter("elementData")
    void setElementData(Object[] elementData);

    @Field.Getter("modCount")
    MyInteger modCount();

    @Field.Setter("modCount")
    void modCount(MyInteger modCount);
}
