package io.github.hhy50.linker.test.v1.importstatic;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.ImportStatic;
import io.github.hhy50.linker.annotations.Target;
import org.junit.Assert;
import org.junit.Test;

public class ImportStaticTest {

    public static class StaticHolder {
        public static String pubField = "pub_field";
        private static String privField = "priv_field";
        public static int counter = 0;

        public static String pubMethod(String name) {
            return "pub_" + name;
        }

        public static String name() {
            return "static_name";
        }

        private static String privMethod(String name) {
            return "priv_" + name;
        }

        static String defaultMethod(String name) {
            return "default_" + name;
        }
    }

    public static class StaticHolder2 {
        public static String pubField2 = "pub_field2";

        public static String pubMethod2(String name) {
            return "pub2_" + name;
        }
    }

    public static class MyTarget {
        public String name() {
            return "target";
        }

        public String dup() {
            return "target_dup";
        }
    }

    @Target.Bind("io.github.hhy50.linker.test.v1.importstatic.ImportStaticTest$MyTarget")
    @ImportStatic(StaticHolder.class)
    public interface MyLinker {

        String name(); // this 的方法优先于 StaticHolder.name()

        String dup();

        String pubMethod(String name);

        String privMethod(String name);

        String defaultMethod(String name);

        @Field.Getter("pubField")
        String getPubField();

        @Field.Setter("pubField")
        void setPubField(String val);

        @Field.Getter("privField")
        String getPrivField();

        @ImportStatic(classes = {"io.github.hhy50.linker.test.v1.importstatic.ImportStaticTest$StaticHolder2," +
                " io.github.hhy50.linker.test.v1.importstatic.ImportStaticTest$StaticHolder"})
        String pubMethod2(String name);

        @ImportStatic(classes = {"io.github.hhy50.linker.test.v1.importstatic.ImportStaticTest$StaticHolder2," +
                " io.github.hhy50.linker.test.v1.importstatic.ImportStaticTest$StaticHolder"})
        @Field.Getter("pubField2")
        String getPubField2();
    }

    @Test
    public void testImportStatic() throws Exception {
        MyLinker linker = LinkerFactory.createLinker(MyLinker.class, new MyTarget());
        // this 优先
        Assert.assertEquals("target", linker.name());
        Assert.assertEquals("target_dup", linker.dup());

        // public 静态方法 -> 直接 invokestatic
        Assert.assertEquals("pub_linker", linker.pubMethod("linker"));
        // private 静态方法 -> MethodHandle
        Assert.assertEquals("priv_linker", linker.privMethod("linker"));
        // default(package-private) 静态方法 -> MethodHandle
        Assert.assertEquals("default_linker", linker.defaultMethod("linker"));

        // public 静态字段 getter/setter
        Assert.assertEquals("pub_field", linker.getPubField());
        linker.setPubField("new_val");
        Assert.assertEquals("new_val", linker.getPubField());
        StaticHolder.pubField = "pub_field"; // 还原

        // private 静态字段 getter
        Assert.assertEquals("priv_field", linker.getPrivField());

        // 方法级 @ImportStatic, classes() 逗号分割, 同包内非 public 内部类按名字加载
        Assert.assertEquals("pub2_linker", linker.pubMethod2("linker"));
        Assert.assertEquals("pub_field2", linker.getPubField2());
    }
}
