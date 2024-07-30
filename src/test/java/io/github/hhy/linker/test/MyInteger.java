package io.github.hhy.linker.test;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;


@Target.Bind("java.lang.Integer")
public interface MyInteger {

    @Field.Getter("value")
    int getValue();
}
