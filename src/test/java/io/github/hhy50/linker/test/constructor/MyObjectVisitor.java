package io.github.hhy50.linker.test.constructor;

import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Typed;

public interface MyObjectVisitor {

    @Method.Constructor
    MyObjectVisitor newInstance1(String name);

    @Method.Constructor
    MyObjectVisitor newInstance2(@Typed(value = "java.lang.String") Object name);
}
