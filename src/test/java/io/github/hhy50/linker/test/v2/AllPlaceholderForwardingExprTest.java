package io.github.hhy50.linker.test.v2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class AllPlaceholderForwardingExprTest {

    public interface AllPlaceholderForwardingLinker {

        @Method.Expr("join(..)")
        String forwardTypedArgsWithAllPlaceholder(String label, UserValue user, int stage);

        @Method.Expr("join(..)")
        String forwardObjectArgsToTypedMethod(Object label, Object user, Object stage);

        @Method.Expr("capture(..).mirror(..)")
        String forwardAllArgsAcrossMultipleSteps(String label, UserValue user, int stage);

        @Method.Expr("spawn(..)")
        UserValue createUserFromObjectReturnWithAllPlaceholder(String prefix, int id);

        @Method.Expr("spawnTyped(..)")
        Object createObjectFromTypedReturnWithAllPlaceholder(String prefix, int id);

        @Method.Expr("ping(..)")
        String forwardEmptyAllPlaceholder();
    }

    public static class UserValue {
        private final String name;

        public UserValue(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class AllPlaceholderTarget {
        private String trace;

        public String join(String label, UserValue user, Integer stage) {
            return label + ":" + user.getName() + ":" + stage;
        }

        public AllPlaceholderTarget capture(Object label, UserValue user, Integer stage) {
            this.trace = String.valueOf(label) + ":" + user.getName() + ":" + stage;
            return this;
        }

        public String mirror(String label, Object user, int stage) {
            return trace + "|" + label + ":" + ((UserValue) user).getName() + ":" + stage;
        }

        public Object spawn(String prefix, Integer id) {
            return new UserValue(prefix + "-" + id);
        }

        public UserValue spawnTyped(String prefix, Integer id) {
            return new UserValue(prefix + "-" + id);
        }

        public String ping() {
            return "signal";
        }
    }

    @Test
    public void shouldForwardAllArgumentsWithDoubleDotExpression() throws LinkerException {
        AllPlaceholderForwardingLinker linker = LinkerFactory.createLinker(
                AllPlaceholderForwardingLinker.class,
                new AllPlaceholderTarget()
        );
        UserValue user = new UserValue("nova");

        Assert.assertEquals("beam:nova:8", linker.forwardTypedArgsWithAllPlaceholder("beam", user, 8));
        Assert.assertEquals("beam:nova:8", linker.forwardObjectArgsToTypedMethod("beam", user, 8));
        Assert.assertEquals(
                "beam:nova:8|beam:nova:8",
                linker.forwardAllArgsAcrossMultipleSteps("beam", user, 8)
        );
    }

    @Test
    public void shouldSupportReturnCastingAndEmptyArgumentForwardingWithDoubleDotExpression() throws LinkerException {
        AllPlaceholderForwardingLinker linker = LinkerFactory.createLinker(
                AllPlaceholderForwardingLinker.class,
                new AllPlaceholderTarget()
        );

        Assert.assertEquals("axis-12", linker.createUserFromObjectReturnWithAllPlaceholder("axis", 12).getName());

        Object userAsObject = linker.createObjectFromTypedReturnWithAllPlaceholder("pulse", 15);
        Assert.assertTrue(userAsObject instanceof UserValue);
        Assert.assertEquals("pulse-15", ((UserValue) userAsObject).getName());

        Assert.assertEquals("signal", linker.forwardEmptyAllPlaceholder());
    }
}
