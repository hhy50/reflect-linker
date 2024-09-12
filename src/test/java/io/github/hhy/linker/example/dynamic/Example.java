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
    /**
     * <p>Getter for the field <code>val</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVal() {
        return val;
    /**
     * <p>Constructor for A2.</p>
     *
     * @param b a {@link io.github.hhy.linker.example.dynamic.B} object.
     */
    }
}
/**
 * <p>getBval.</p>
 *
 * @return a {@link java.lang.String} object.
 */

class A2 extends A {
    public B b;
    public A2(B b) {
        this.b = b;
    }
}
/**
 * <p>setA.</p>
 *
 * @param val a {@link io.github.hhy.linker.example.dynamic.AVisitor} object.
 */
class MyObject {
    private A a;

    /**
     * <p>getB.</p>
     *
     * @return a {@link io.github.hhy.linker.example.dynamic.B} object.
     */
    public String getBval() {
        /**
         * <p>main.</p>
         *
         * @param args an array of {@link java.lang.String} objects.
         * @throws io.github.hhy.linker.exceptions.LinkerException if any.
         */
        return ((A2)a).b.getVal();
    }
/**
 * <p>getBval.</p>
 *
 * @return a {@link java.lang.String} object.
 */
}

/**
 * <p>getBval2.</p>
 *
 * @return a {@link java.lang.String} object.
 */
@Target.Bind("io.github.hhy.linker.example.dynamic.A")
interface AVisitor {

}

@Target.Bind("io.github.hhy.linker.example.dynamic.MyObject")
interface MyObjectVisitor {
    @Field.Setter("a")
    void setA(AVisitor val);

    @Field.Getter("a.b")
    B getB();

    String getBval();

    @Typed(name = "a", type = "io.github.hhy.linker.example.dynamic.A2")
    @Method.Name("a.b.getVal")
    String getBval2();
}

class Example {
    public static void main(String[] args) throws LinkerException {
        MyObject myObj = new MyObject();
        MyObjectVisitor myObjVisitor = LinkerFactory.createLinker(MyObjectVisitor.class, myObj);
        myObjVisitor.setA(LinkerFactory.createLinker(AVisitor.class, new A2(new B())));

        System.out.println(myObjVisitor.getB().val);
        System.out.println(myObjVisitor.getB().getVal());
        System.out.println(myObjVisitor.getBval());
        System.out.println(myObjVisitor.getBval2());

        // do something
    }
}
