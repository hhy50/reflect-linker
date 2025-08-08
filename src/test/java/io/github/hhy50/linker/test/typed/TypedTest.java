package io.github.hhy50.linker.test.typed;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Typed;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class TypedTest {

    interface TypedLinker {
        public Object getString(@Typed(value = "java.lang.String") Object obj);

        @Typed(name = "user3", value = "io.github.hhy50.linker.test.typed.User3")
        @Typed(name = "user2", value = "io.github.hhy50.linker.test.typed.User2")
        @Method.Expr("user2.user3.getString($0).toString()")
        public Object getUser3String(@Typed(value = "java.lang.String") Object obj);


//        @Typed(name = "users[1]", value = "io.github.hhy50.linker.test.typed.User2")
        @Typed(name = "users", value = "io.github.hhy50.linker.test.typed.User2[]")
        @Method.Expr("users[1].user3.getString($0)")
        public String getUsers1String(String a);
    }

    @Test
    public void test1() throws LinkerException {
        TypedLinker linker = LinkerFactory.createLinker(TypedLinker.class, new User());
//        MethodParamsTyped linker = new MethodParamsTyped$impl(new User());
        Assert.assertEquals(linker.getString(""), "string");
        Assert.assertEquals(linker.getUser3String(""), "string");
        Assert.assertEquals("string", linker.getUsers1String(""));
    }
}
