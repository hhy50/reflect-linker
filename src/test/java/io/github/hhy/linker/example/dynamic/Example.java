package io.github.hhy.linker.example.dynamic;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Method;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.annotations.Typed;
import io.github.hhy.linker.exceptions.LinkerException;

class A { }
class B {
    String val = "this is b'val";
    public String getVal() {
        return val;
    }
}

class A2 extends A {
    private B b;
    public A2(B b) {
        this.b = b;
    }
}
class MyObject {
    private A a;
}

@Target.Bind("io.github.hhy.linker.example.dynamic.MyObject")
interface MyObjectVisitor {
    @Field.Setter("a")
    void setA(A val);

    @Field.Getter("a.b")
    B getB();

    @Typed(name = "a", type = "io.github.hhy.linker.example.dynamic.A2")
    @Method.Name("a.b.getVal")
    String getBval();
}

class Example {
    public static void main(String[] args) throws LinkerException {
        MyObject myObj = new MyObject();
        MyObjectVisitor myObjVisitor = LinkerFactory.createLinker(MyObjectVisitor.class, myObj);
        myObjVisitor.setA(new A2(new B()));

        System.out.println(myObjVisitor.getB().val);
        System.out.println(myObjVisitor.getB().getVal());
        System.out.println(myObjVisitor.getBval());

        // do something
    }
}