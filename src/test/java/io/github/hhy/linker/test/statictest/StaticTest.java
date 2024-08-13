package io.github.hhy.linker.test.statictest;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.exceptions.LinkerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StaticTest {


    @Test
    public void test1() throws LinkerException {
        MyStaticClass linker = LinkerFactory.createLinker(MyStaticClass.class, new StaticClass());
        Assertions.assertTrue(linker.getA() == StaticClass.getA());
    }
}
