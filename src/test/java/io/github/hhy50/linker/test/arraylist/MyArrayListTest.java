package io.github.hhy50.linker.test.arraylist;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.define.provider.TargetProvider;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * <p>MyArrayListTest class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class MyArrayListTest {

    /**
     * <p>test.</p>
     *
     * @throws LinkerException if any.
     */
    @Test
    public void test() throws LinkerException {
        Object[] objects = new Object[10];

        MyArrayList staticLinker = LinkerFactory.createStaticLinker(MyArrayList.class, ArrayList.class);
        MyArrayList list = staticLinker.newList();
        list.setElementData(objects);

        Assert.assertTrue(list instanceof TargetProvider);
        Assert.assertTrue(objects == list.getElementData());

        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add("5");
        Assert.assertEquals(objects[0], list.get(0));
        Assert.assertEquals(objects[1], list.get(1));
        Assert.assertEquals(objects[2], list.get(2));
        Assert.assertEquals(objects[3], list.get(3));
        Assert.assertEquals(objects[4], list.get(4));
        Assert.assertEquals(list.size(), list.size());

        MyArrayList3 list3 = LinkerFactory.createLinker(MyArrayList3.class, new ArrayList<>());
        list3.add(1);
        list3.add(2);
        list3.add(3);
        list3.add(4);
        list3.add("5");
        Assert.assertEquals(1, list3.get(0));
        Assert.assertEquals(2, list3.get(1));
        Assert.assertEquals(3, list3.get(2));
        Assert.assertEquals(4, list3.get(3));
        Assert.assertEquals("5", list3.get(4));

        LinkerFactory.createLinker(MyArrayList.class, new LinkedList<>());
        LinkerFactory.createLinker(MyArrayList.class, new Vector<>());
        LinkerFactory.createLinker(MyArrayList.class, new CopyOnWriteArrayList<>());
    }

    public static void check() {

    }
}
