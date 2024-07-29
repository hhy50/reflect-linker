package io.github.hhy.linker.test;

import io.github.hhy.linker.LinkerFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MyArrayListTest {


    @Test
    public void test() {

        MyArrayList list = LinkerFactory.newInstance(MyArrayList.class, ArrayList.class);
        Object[] elementData = list.getElementData();

    }
}
