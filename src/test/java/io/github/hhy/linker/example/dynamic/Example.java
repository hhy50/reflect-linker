package io.github.hhy.linker.example.dynamic;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.annotations.Typed;
import io.github.hhy.linker.exceptions.LinkerException;

class A { }
class B {
    String val = "this is b'val";
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

    @Typed(name = "a", type = "io.github.hhy.linker.example.dynamic.A2")
    @Field.Getter("a.b")
    B getB();
}

class Example {
    public static void main(String[] args) throws LinkerException {
        MyObject myObj = new MyObject();
        MyObjectVisitor myObjVisitor = LinkerFactory.createLinker(MyObjectVisitor.class, myObj);
        myObjVisitor.setA(new A2(new B()));
        System.out.println(myObjVisitor.getB().val);

        // do something
    }
}