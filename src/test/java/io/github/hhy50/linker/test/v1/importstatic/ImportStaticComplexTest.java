package io.github.hhy50.linker.test.v1.importstatic;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.ImportStatic;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Target;
import org.junit.Assert;
import org.junit.Test;

/**
 * @ImportStatic 复杂场景测试
 */
public class ImportStaticComplexTest {

    public static class UtilA {
        public static String CONST = "const_A";
        public static String shared = "A_shared";
        private static int count;

        public static UtilA self = new UtilA();

        // 与目标类实例方法签名完全相同
        public static String sameSig(String s) {
            return "A_same_" + s;
        }

        public static String mixed(int i, String s) {
            return "A_mixed_" + i + "_" + s;
        }

        public static String pick() {
            return "A_pick";
        }

        // 重载
        public static String f(String s) {
            return "A_f_str_" + s;
        }

        public static String f(int i) {
            return "A_f_int_" + i;
        }

        private static String privOnly() {
            return "A_priv";
        }

        public String instanceOnSelf() {
            return "instance_on_A";
        }
    }

    public static class UtilB {
        // 与 UtilA 同名，方法级导入时 UtilB 在前则优先
        public static String pick() {
            return "B_pick";
        }

        public static String f(String s) {
            return "B_f_str_" + s;
        }
    }

    public static class Ctx {
        public String shared = "ctx_shared";

        // 与 UtilA.sameSig(String) 签名完全相同的实例方法 -> this 优先
        public String sameSig(String s) {
            return "ctx_same_" + s;
        }

        // 与 UtilA.mixed(int, String) 签名完全相同的实例方法 -> this 优先
        public String mixed(int i, String s) {
            return "ctx_mixed_" + i + "_" + s;
        }
    }

    @Target.Bind("io.github.hhy50.linker.test.v1.importstatic.ImportStaticComplexTest$Ctx")
    @ImportStatic(UtilA.class)
    public interface L {

        // ===== this 优先（签名完全相同的实例方法 vs 导入的静态方法） =====
        String sameSig(String s);

        String mixed(int i, String s);

        // 字段同名：this 的实例字段优先
        @Field.Getter("shared")
        String getShared();

        // ===== 仅在导入类中存在 =====
        String pick(); // UtilA.pick

        String privOnly(); // UtilA 的 private 静态方法

        // ===== 重载解析 =====
        String f(String s); // A_f_str

        String f(int i); // A_f_int

        // ===== 静态字段 =====
        @Field.Getter("CONST")
        String getConst();

        @Field.Getter("count")
        int getCount();

        @Field.Setter("count")
        void setCount(int c); // private 静态字段 setter

        // ===== 链式表达式：导入的静态成员作为链头 =====

        // 静态字段 -> 实例方法
        @Method.Expr("self.instanceOnSelf()")
        String chainedFieldToInstance();

        // 静态字段 -> 实例方法（String.length）
        @Method.Expr("CONST.length()")
        int chainedFieldLength();

        // 静态方法作为链头 -> 后接实例方法
        @Method.Expr("pick().length()")
        int chainedStaticMethodHead();

        // ===== 方法级 @ImportStatic（完全覆盖类级，且按声明顺序优先） =====

        // pick: UtilB 在前 -> B_pick
        @Method.Expr("pick()")
        @ImportStatic(classes = {"io.github.hhy50.linker.test.v1.importstatic.ImportStaticComplexTest$UtilB," +
                " io.github.hhy50.linker.test.v1.importstatic.ImportStaticComplexTest$UtilA"})
        String pick2();

        // f(String): UtilB 在前 -> B_f_str
        @Method.Expr("f($0)")
        @ImportStatic(classes = {"io.github.hhy50.linker.test.v1.importstatic.ImportStaticComplexTest$UtilB," +
                " io.github.hhy50.linker.test.v1.importstatic.ImportStaticComplexTest$UtilA"})
        String f2(String s);

        // 方法级只导入 UtilB（覆盖类级 UtilA），但 this 的实例方法 sameSig 依然优先命中
        @Method.Expr("sameSig($0)")
        @ImportStatic(UtilB.class)
        String sameSig2(String s);
    }

    @Test
    public void testComplex() throws Exception {
        L linker = LinkerFactory.createLinker(L.class, new Ctx());

        // this 优先（签名完全相同）
        Assert.assertEquals("ctx_same_x", linker.sameSig("x"));
        Assert.assertEquals("ctx_mixed_1_y", linker.mixed(1, "y"));
        Assert.assertEquals("ctx_shared", linker.getShared());

        // 仅导入类中存在
        Assert.assertEquals("A_pick", linker.pick());
        Assert.assertEquals("A_priv", linker.privOnly());

        // 重载
        Assert.assertEquals("A_f_str_s", linker.f("s"));
        Assert.assertEquals("A_f_int_9", linker.f(9));

        // 静态字段
        Assert.assertEquals("const_A", linker.getConst());
        Assert.assertEquals(0, linker.getCount());
        linker.setCount(42);
        Assert.assertEquals(42, linker.getCount());
        UtilA.count = 0; // 还原

        // 链式表达式
        Assert.assertEquals("instance_on_A", linker.chainedFieldToInstance());
        Assert.assertEquals("const_A".length(), linker.chainedFieldLength());
        Assert.assertEquals("A_pick".length(), linker.chainedStaticMethodHead());

        // 方法级导入，声明顺序优先
        Assert.assertEquals("B_pick", linker.pick2());
        Assert.assertEquals("B_f_str_s", linker.f2("s"));

        // 方法级覆盖类级后，this 依然优先
        Assert.assertEquals("ctx_same_x", linker.sameSig2("x"));
    }
}
