package io.github.hhy50.linker.test.typed;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Typed;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class TypedTest {

    interface TypedLinker {
        public Object getString(@Typed(type = "java.lang.String") Object obj);

        @Typed(name = "user3", type = "io.github.hhy50.linker.test.typed.User3")
        @Typed(name = "user2", type = "io.github.hhy50.linker.test.typed.User2")
        @Method.Name("user2.user3.getString")
        public Object getUser3String(@Typed(type = "java.lang.String") Object obj);
    }



    @Test
    public void test1() throws LinkerException {
        TypedLinker linker = LinkerFactory.createLinker(TypedLinker.class, new User());
//        MethodParamsTyped linker = new MethodParamsTyped$impl(new User());
        Assert.assertEquals(linker.getString(""), "string");
        Assert.assertEquals(linker.getUser3String(""), "string");
    }
}
