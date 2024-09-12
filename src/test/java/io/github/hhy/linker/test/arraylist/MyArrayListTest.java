package io.github.hhy.linker.test.arraylist;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.define.provider.TargetProvider;
import io.github.hhy.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

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
     * @throws io.github.hhy.linker.exceptions.LinkerException if any.
     */
    @Test
    public void test() throws LinkerException {
        Object[] objects = new Object[10];
        MyArrayList list = LinkerFactory.createLinker(MyArrayList.class, new ArrayList<>());
        list.setElementData(objects);

        Assert.assertTrue(list instanceof TargetProvider);
        Assert.assertTrue(objects == list.getElementData());

        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add("5");
        Assert.assertEquals(objects[0], 1);
        Assert.assertEquals(objects[1], 2);
        Assert.assertEquals(objects[2], 3);
        Assert.assertEquals(objects[3], 4);
        Assert.assertEquals(objects[4], "5");
        Assert.assertEquals(list.size(), 5);
    }
}
