package io.github.hhy50.linker.test.typed;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class TypedTest {


    @Test
    public void test1() throws LinkerException {
        MethodParamsTyped linker = LinkerFactory.createLinker(MethodParamsTyped.class, new User());
//        MethodParamsTyped linker = new MethodParamsTyped$impl(new User());
        Assert.assertEquals(linker.getString(""), "string");
        Assert.assertEquals(linker.getUser3String(""), "string");
    }
}
