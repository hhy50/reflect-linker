package io.github.hhy.linker.test.arraylist;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.define.provider.TargetProvider;
import io.github.hhy.linker.exceptions.LinkerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MyArrayListTest {


    @Test
    public void test() throws LinkerException {
        Object[] objects = new Object[10];
        MyArrayList list = LinkerFactory.createLinker(MyArrayList.class, new ArrayList<>());
        list.setElementData(objects);

        Assertions.assertTrue(list instanceof TargetProvider);
        Assertions.assertTrue(objects == list.getElementData());
        Assertions.assertTrue(objects.length == list.cap());
    }
}
