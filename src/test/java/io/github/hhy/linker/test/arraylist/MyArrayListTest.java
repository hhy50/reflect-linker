package io.github.hhy.linker.test.arraylist;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.define.provider.TargetProvider;
import io.github.hhy.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class MyArrayListTest {




    @Test
    public void test() throws LinkerException {
        Object[] objects = new Object[10];
        MyArrayList list = LinkerFactory.createLinker(MyArrayList.class, new ArrayList<>());
        list.setElementData(objects);

        Assert.assertTrue(list instanceof TargetProvider);
        Assert.assertTrue(objects == list.getElementData());
    }
}
