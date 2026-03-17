package io.github.hhy50.linker.test.v1.arraylist;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Test;

import java.util.ArrayList;

/**
 * <p>LArrayListTest class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class ArrayListTest2 {

    @Test
    public void test() throws LinkerException, ClassNotFoundException {
        Object[] objects = new Object[10];
//        LArrayList2 staticLinker = LinkerFactory.createStaticLinker(LArrayList2.class, ArrayList.class);
//        LArrayList2 list = staticLinker.newList();
        LArrayList2 list = LinkerFactory.createLinker(LArrayList2.class, new ArrayList());
        list.setElementData(objects);

        list.addInt(5555);
        list.addInt1();
        list.addInt2(5555);

        list.addStr("6666");
        list.addStr1();
        list.addStr2("6666");
    }
}
