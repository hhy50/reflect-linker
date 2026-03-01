package io.github.hhy50.linker.test.arraylist;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.generate.builtin.TargetProvider;

import java.util.List;

public interface LArrayList2 extends TargetProvider<List> {

    @Method.Constructor
    LArrayList2 newList();

    /**
     * <p>add.</p>
     *
     */
    @Method.Expr("add(..)")
    void addStr(String str);
    @Method.Expr("add('1234')")
    void addStr1();
    @Method.Expr("add('1234')")
    void addStr2(String str);

    @Method.Expr("add(..)")
    void addInt(Integer i);
    @Method.Expr("add(1234)")
    void addInt1();
    @Method.Expr("add(1234)")
    void addInt2(Integer i);

    @Method.Expr("get(..)")
    Object get(Object i);

    @Field.Setter("elementData")
    void setElementData(Object elementData);
}
