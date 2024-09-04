package io.github.hhy.linker.test.arraylist;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.test.MyInteger;


@Target.Bind("java.util.ArrayList")
public interface MyArrayList {

    Boolean add(Object o);

    MyInteger size();

    @Field.Getter("elementData")
    Object getElementData();

    @Field.Setter("elementData")
    void setElementData(Object elementData);

    @Field.Getter("modCount")
    int modCount();
}
