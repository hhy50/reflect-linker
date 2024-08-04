package io.github.hhy.linker.test;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.define.TargetProvider;
import io.github.hhy.linker.exceptions.LinkerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MyArrayListTest {


    @Test
    public void test() throws LinkerException {
        Object[] objects = new Object[10];

        MyArrayList list = LinkerFactory.newInstance(MyArrayList.class, ArrayList.class);
        Assertions.assertTrue(list instanceof TargetProvider);

        list.setElementData(objects);
        Object[] elementData = list.getElementData();

        Assertions.assertTrue(objects==elementData);
    }
}
