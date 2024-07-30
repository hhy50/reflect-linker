package io.github.hhy.linker.test;

import io.github.hhy.linker.LinkerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MyArrayListTest {


    @Test
    public void test() {
        Object[] objects = new Object[10];
        MyArrayList list = LinkerFactory.newInstance(MyArrayList.class, ArrayList.class);
        list.elementData(objects);
        Object[] elementData = list.elementData();
        Assertions.assertTrue(objects==elementData);
    }
}
