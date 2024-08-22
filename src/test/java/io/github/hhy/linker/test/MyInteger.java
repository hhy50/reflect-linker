package io.github.hhy.linker.test;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;


@Target.Bind("java.lang.Object")
public interface MyInteger {

//    @Typed(name = "target", type = "java.lang.Integer")
    @Field.Getter("value")
    int getValue();
}
