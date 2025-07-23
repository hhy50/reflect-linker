package io.github.hhy50.linker.test.nest.case5;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Target;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;

public class Test {

    interface LOut {
        @Field.Getter("inner")
        LInner getInner();

        @Field.Setter("inner")
        void setInner(LInner inner);

        @Method.Expr("toString()")
        String out_to_string();

        @Method.Expr("inner.toString()")
        String inner_tostring();
    }

    @Target.Bind("io.github.hhy50.linker.test.nest.case5.Out$Inner")
    interface LInner {
        @Method.Expr("toString()")
        String toString();

        @Method.InvokeSuper
        @Method.Expr("toString()")
        String superToString();
    }

    @org.junit.Test
    public void test1() throws LinkerException {
        LOut out = LinkerFactory.createLinker(LOut.class, new Out());
        LInner inner = LinkerFactory.createLinker(LInner.class, new Out.Inner());
        out.setInner(inner);

        Assert.assertNotNull(out.getInner());
        Assert.assertEquals(out.out_to_string(), "i am out");
        Assert.assertEquals(out.getInner().toString(), "i am inner");
        Assert.assertNotEquals(out.getInner().superToString(), "i am inner");
    }
}
