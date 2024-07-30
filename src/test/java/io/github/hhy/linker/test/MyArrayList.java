package io.github.hhy.linker.test;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;


@Target.Bind("java.util.ArrayList")
public interface MyArrayList {

    void add(Object o);

    @Field.Getter()
    Object[] elementData();

    @Field.Setter
    void elementData(Object[] elementData);
}
