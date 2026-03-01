package io.github.hhy50.linker.test.arraylist;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.annotations.Field;
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

    interface LClass {
        @Field.Getter("module")
        LModule getModule();
    }

    interface JdkCompilerModule {
        void implAddOpens(String pn, LModule other);
    }

    @Autolink
    interface LModule {
        @io.github.hhy50.linker.annotations.Method.Expr("boot().findModule('jdk.compiler').get()")
        JdkCompilerModule getJdkCompilerModule();
    }


    @Test
    public void test() throws LinkerException, ClassNotFoundException {
        LModule moduleLayer = LinkerFactory.createStaticLinker(LModule.class, Class.forName("java.lang.ModuleLayer"));

        LModule processorModule = LinkerFactory.createLinker(LClass.class, ArrayListTest2.class).getModule();
        moduleLayer.getJdkCompilerModule().implAddOpens("", null);

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
