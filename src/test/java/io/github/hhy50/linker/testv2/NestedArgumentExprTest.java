package io.github.hhy50.linker.testv2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class NestedArgumentExprTest {

    public interface NestedArgumentExprLinker {

        @Method.Expr("a.b.c.doWithBranch(a.b, $0)")
        String forwardFieldReference(String suffix);

        @Method.Expr("a.b.c.join(a.b.code, a.b.decorate($0))")
        String forwardFieldAndMethodArguments(String suffix);

        @Method.Expr("a.b.c.join(a.b.readCode(), a.b.makeLabel($0).finish())")
        String forwardMethodChainArguments(String suffix);
    }

    public static class Root {
        private final A a;

        public Root(A a) {
            this.a = a;
        }
    }

    public static class A {
        private final B b;

        public A(B b) {
            this.b = b;
        }
    }

    public static class B {
        private final C c;
        private final String code;

        public B(C c, String code) {
            this.c = c;
            this.code = code;
        }

        public String decorate(String suffix) {
            return code + "-" + suffix;
        }

        public String readCode() {
            return code;
        }

        public Label makeLabel(String suffix) {
            return new Label(code + "-" + suffix);
        }
    }

    public static class Label {
        private final String value;

        public Label(String value) {
            this.value = value;
        }

        public String finish() {
            return value;
        }
    }

    public static class C {
        public String doWithBranch(B branch, String suffix) {
            return branch.code + ":" + suffix;
        }

        public String join(String left, String right) {
            return left + "|" + right;
        }
    }

    @Test
    public void shouldSupportFieldAndMethodExpressionsInsideArguments() throws LinkerException {
        NestedArgumentExprLinker linker = LinkerFactory.createLinker(
                NestedArgumentExprLinker.class,
                new Root(new A(new B(new C(), "branch")))
        );

        Assert.assertEquals("branch:tail", linker.forwardFieldReference("tail"));
        Assert.assertEquals("branch|branch-tail", linker.forwardFieldAndMethodArguments("tail"));
        Assert.assertEquals("branch|branch-tail", linker.forwardMethodChainArguments("tail"));
    }
}
