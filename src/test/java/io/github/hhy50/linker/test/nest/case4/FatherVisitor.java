package io.github.hhy50.linker.test.nest.case4;


import io.github.hhy50.linker.annotations.Method;

public interface FatherVisitor {

    @Method.InvokeSuper("io.github.hhy50.linker.test.nest.case4.Father1")
    @Method.Expr("aaa()")
    public String father1_aaa();

    @Method.InvokeSuper("io.github.hhy50.linker.test.nest.case4.Father2")
    @Method.Expr("aaa()")
    public String father2_aaa();


    @Method.InvokeSuper("io.github.hhy50.linker.test.nest.case4.Father3")
    @Method.Expr("aaa()")
    public String father3_aaa();

    @Method.InvokeSuper
    @Method.Expr("aaa()")
    public String father_aaa();

    @Method.Expr("aaa()")
    public String my_aaa();
}
