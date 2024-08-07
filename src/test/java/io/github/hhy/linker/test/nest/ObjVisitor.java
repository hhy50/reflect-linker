package io.github.hhy.linker.test.nest;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;

@Target.Bind("io.github.hhy.linker.test.nest.Obj")
public interface ObjVisitor {
    @Field.Getter("a")
    Object getA();

    @Field.Getter("a.b")
    Object getB();

    @Field.Getter("a.b.c")
    Object getC();

    @Field.Getter("a.c")
    Object getC2();

    @Field.Getter("a.b.c.str")
    String getStr();
}
