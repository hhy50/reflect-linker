package io.github.hhy50.linker.test.v1.arraylist;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Test;

import javax.annotation.processing.Processor;
import java.util.ArrayList;

/**
 * <p>LArrayListTest class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class ArrayListTest2 {

    interface JdkCompilerModule {
        @Method.Expr("implAddOpens($0, class('com.chy.lamia.processor.MappingAnnotationProcessor').getModule())")
        void implAddOpens(String pn);
    }

    @Autolink
    interface LModule {
        @Method.Expr("boot().findModule('jdk.compiler').get()")
        JdkCompilerModule getJdkCompilerModule();
    }

    interface ProcessCreator extends Processor {
        @Method.Constructor
        ProcessCreator newInstance();
    }

    @Test
    public void test() throws LinkerException, ClassNotFoundException {
        LinkerFactory.createStaticLinker(JdkCompilerModule.class, LModule.class);

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
