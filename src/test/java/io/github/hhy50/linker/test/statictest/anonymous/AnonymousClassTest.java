package io.github.hhy50.linker.test.statictest.anonymous;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class AnonymousClassTest {
    static class A {
        static String run() {
            return "1";
        }
    }

    static class B {
        static String run() {
            return "2";
        }
    }


    @Test
    public void test() throws LinkerException {
        Linker aObj = LinkerFactory.createStaticLinker(Linker.class, A.class);
        Linker bObj = LinkerFactory.createStaticLinker(Linker.class, B.class);
        Assert.assertEquals("1", aObj.run());
        Assert.assertEquals("2", bObj.run());
    }
}
