package io.github.hhy50.linker.test.typed;

import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Typed;

public interface MethodParamsTyped {

    public Object getString(@Typed(type = "java.lang.String") Object obj);


    @Typed(name = "user3", type = "io.github.hhy50.linker.test.typed.User3")
    @Typed(name = "user2", type = "io.github.hhy50.linker.test.typed.User2")
    @Method.Name("user2.user3.getString")
    public Object getUser3String(@Typed(type = "java.lang.String") Object obj);
}
