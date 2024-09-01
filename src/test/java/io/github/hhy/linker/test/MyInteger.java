package io.github.hhy.linker.test;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.annotations.Typed;


//@Target.Bind("java.lang.Object")
@Target.Bind("java.lang.Integer")
public interface MyInteger {

    @Typed(name = "target", type = "java.lang.Integer")
    @Field.Getter("value")
    int getValue();
}
