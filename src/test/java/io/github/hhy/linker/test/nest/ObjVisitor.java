package io.github.hhy.linker.test.nest;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;

//@Typed(name = "a", type = "io.github.hhy.linker.test.nest.A2")
@Target.Bind("io.github.hhy.linker.test.nest.Obj")
public interface ObjVisitor {
    @Field.Getter("a")
    Object getA();

    @Field.Setter("a")
    void setA(Object a);

    @Field.Getter("a.b")
    Object getB();

    @Field.Setter("a.b")
    void setB(Object b);

    @Field.Getter("a.b.c")
    Object getC();

    @Field.Setter("a.b.c")
    void setC(Object c);

    @Field.Getter("a.c")
    Object getC2();

    @Field.Setter("a.c")
    void setC2(Object c);

    @Field.Getter("a.b.c.str")
    String getStr();

    @Field.Setter("a.b.c.str")
    void setStr(Object c);

    @Field.Getter("a.c.str")
    String getStr2();

    @Field.Setter("a.c.str")
    void setStr2(String str2);

    @Field.Getter("a.d")
    Object getD();

    @Field.Setter("a.d")
    void setD(int d);
}
