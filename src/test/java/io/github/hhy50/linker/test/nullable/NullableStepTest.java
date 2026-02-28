package io.github.hhy50.linker.test.nullable;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Target;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class NullableStepTest {

    static class Root {
        Node node;
    }

    static class Node {
        String name;
        Leaf leaf;
    }

    static class Leaf {
        int value;
    }

    @Target.Bind("io.github.hhy50.linker.test.nullable.NullableStepTest$Root")
    interface RootVisitor {

        @Field.Getter("node?.name")
        String getName();

        @Field.Getter("node?.leaf?.value")
        int getLeafValue();
    }

    @Test
    public void testNullableFieldStepReturnDefault() throws LinkerException {
        Root root = new Root();
        RootVisitor visitor = LinkerFactory.createLinker(RootVisitor.class, root);

        Assert.assertNull(visitor.getName());
        Assert.assertEquals(0, visitor.getLeafValue());

        root.node = new Node();
        root.node.name = "node";

        Assert.assertEquals("node", visitor.getName());
        Assert.assertEquals(0, visitor.getLeafValue());

        root.node.leaf = new Leaf();
        root.node.leaf.value = 12;

        Assert.assertEquals(12, visitor.getLeafValue());
    }
}