package io.github.hhy50.linker.test.testv2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class LongNullableChainAccessTest {

    public interface LongNullableChainLinker {

        @Field.Getter("a.b.c.d?.e.f?.g")
        String readFieldChainString();

        @Field.Getter("a.b.c.d?.e.f?.count")
        int readFieldChainCount();

        @Field.Getter("a.b.c.d?.e.f?.payload")
        UserValue readFieldChainPayloadAsUser();

        @Field.Getter("a.b.c.d?.e.f?.user")
        Object readFieldChainUserAsObject();

        @Method.Expr("a.getB().getC().getD()?.getE().getF()?.getG()")
        String readMethodChainString();

        @Method.Expr("a.getB().getC().getD()?.getE().getF()?.getCount()")
        int readMethodChainCount();

        @Method.Expr("a.getB().getC().getD()?.getE().getF()?.getPayload()")
        UserValue readMethodChainPayloadAsUser();

        @Method.Expr("a.getB().getC().getD()?.getE().getF()?.getUser()")
        Object readMethodChainUserAsObject();

        @Method.Expr("a.getB().getC().getD()?.getE().getF()?.join($0)")
        String readMethodChainJoinedValue(String prefix);
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

    public static class RootTarget {
        private LevelA a;
    }

    public static class LevelA {
        private LevelB b;

        public LevelB getB() {
            return b;
        }
    }

    public static class LevelB {
        private LevelC c;

        public LevelC getC() {
            return c;
        }
    }

    public static class LevelC {
        private LevelD d;

        public LevelD getD() {
            return d;
        }
    }

    public static class LevelD {
        private LevelE e;

        public LevelE getE() {
            return e;
        }
    }

    public static class LevelE {
        private LevelF f;

        public LevelF getF() {
            return f;
        }
    }

    public static class LevelF {
        private String g;
        private int count;
        private UserValue user;
        private Object payload;

        public String getG() {
            return g;
        }

        public int getCount() {
            return count;
        }

        public UserValue getUser() {
            return user;
        }

        public Object getPayload() {
            return payload;
        }

        public String join(String prefix) {
            return prefix + ":" + g + ":" + user.getName();
        }
    }

    @Test
    public void shouldReturnDefaultsForLongNullableFieldChainAndReadValueWhenChainIsComplete() throws LinkerException {
        RootTarget target = createTarget();
        LongNullableChainLinker linker = LinkerFactory.createLinker(LongNullableChainLinker.class, target);

        Assert.assertNull(linker.readFieldChainString());
        Assert.assertEquals(0, linker.readFieldChainCount());
        Assert.assertNull(linker.readFieldChainPayloadAsUser());
        Assert.assertNull(linker.readFieldChainUserAsObject());

        target.a.b.c.d = new LevelD();
        target.a.b.c.d.e = new LevelE();

        Assert.assertNull(linker.readFieldChainString());
        Assert.assertEquals(0, linker.readFieldChainCount());
        Assert.assertNull(linker.readFieldChainPayloadAsUser());
        Assert.assertNull(linker.readFieldChainUserAsObject());

        target.a.b.c.d.e.f = createLeaf("gamma", 9, "neo", "oracle");

        Assert.assertEquals("gamma", linker.readFieldChainString());
        Assert.assertEquals(9, linker.readFieldChainCount());
        Assert.assertEquals("oracle", linker.readFieldChainPayloadAsUser().getName());

        Object userAsObject = linker.readFieldChainUserAsObject();
        Assert.assertTrue(userAsObject instanceof UserValue);
        Assert.assertEquals("neo", ((UserValue) userAsObject).getName());
    }

    @Test
    public void shouldReturnDefaultsForLongNullableMethodChainAndReadValueWhenChainIsComplete() throws LinkerException {
        RootTarget target = createTarget();
        LongNullableChainLinker linker = LinkerFactory.createLinker(LongNullableChainLinker.class, target);

        Assert.assertNull(linker.readMethodChainString());
        Assert.assertEquals(0, linker.readMethodChainCount());
        Assert.assertNull(linker.readMethodChainPayloadAsUser());
        Assert.assertNull(linker.readMethodChainUserAsObject());
        Assert.assertNull(linker.readMethodChainJoinedValue("prefix"));

        target.a.b.c.d = new LevelD();
        target.a.b.c.d.e = new LevelE();

        Assert.assertNull(linker.readMethodChainString());
        Assert.assertEquals(0, linker.readMethodChainCount());
        Assert.assertNull(linker.readMethodChainPayloadAsUser());
        Assert.assertNull(linker.readMethodChainUserAsObject());
        Assert.assertNull(linker.readMethodChainJoinedValue("prefix"));

        target.a.b.c.d.e.f = createLeaf("delta", 12, "trinity", "morpheus");

        Assert.assertEquals("delta", linker.readMethodChainString());
        Assert.assertEquals(12, linker.readMethodChainCount());
        Assert.assertEquals("morpheus", linker.readMethodChainPayloadAsUser().getName());

        Object userAsObject = linker.readMethodChainUserAsObject();
        Assert.assertTrue(userAsObject instanceof UserValue);
        Assert.assertEquals("trinity", ((UserValue) userAsObject).getName());

        Assert.assertEquals("prefix:delta:trinity", linker.readMethodChainJoinedValue("prefix"));
    }

    private RootTarget createTarget() {
        RootTarget target = new RootTarget();
        target.a = new LevelA();
        target.a.b = new LevelB();
        target.a.b.c = new LevelC();
        return target;
    }

    private LevelF createLeaf(String g, int count, String userName, String payloadName) {
        LevelF leaf = new LevelF();
        leaf.g = g;
        leaf.count = count;
        leaf.user = new UserValue(userName);
        leaf.payload = new UserValue(payloadName);
        return leaf;
    }
}
