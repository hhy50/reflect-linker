package io.github.hhy.linker.test;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.define.TargetProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MyIntegerTest {


    @Test
    public void test() {
        MyInteger myInteger = LinkerFactory.createLinker(MyInteger.class, new Integer(10));
        Assertions.assertEquals(10, myInteger.getValue());
    }
}
