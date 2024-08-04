package io.github.hhy.linker.test.nest;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.exceptions.LinkerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class NestObjTest {


    @Test
    public void test1() throws LinkerException {
        ObjVisitor obj = LinkerFactory.createLinker(ObjVisitor.class, new Obj());
        Object a = obj.getA();
        Assertions.assertTrue(a != null);
    }
}