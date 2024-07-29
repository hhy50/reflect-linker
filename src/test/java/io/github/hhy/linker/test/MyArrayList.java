package io.github.hhy.linker.test;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Method;
import io.github.hhy.linker.annotations.Target;


@Target.Bind("java.util.ArrayList")
public interface MyArrayList {

    @Method.Sign("void add(Object)")
    void add(Object o);

    @Field.Getter("elementData")
    Object[] getElementData();

    @Field.Setter("elementData")
    void setElementData(Object[] elementData);
}
