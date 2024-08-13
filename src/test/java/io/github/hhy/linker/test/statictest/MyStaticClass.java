package io.github.hhy.linker.test.statictest;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;

@Target.Bind("io.github.hhy.linker.test.statictest.StaticClass")
public interface MyStaticClass {

    @Field.Getter("aaa")
    public String getA();

    @Field.Getter("aaa2")
    public String getA2();

    @Field.Setter("aaa")
    public void setA(String aaa);
}
