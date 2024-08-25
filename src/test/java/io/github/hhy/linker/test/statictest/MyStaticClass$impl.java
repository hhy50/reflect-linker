//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.hhy.linker.test.statictest;

import io.github.hhy.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy.linker.runtime.Runtime;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;

public class MyStaticClass$impl extends DefaultTargetProviderImpl implements MyStaticClass {
    public static final MethodHandles.Lookup io_github_hhy_linker_test_statictest_StaticClass_lookup;

    static {
        try {
            io_github_hhy_linker_test_statictest_StaticClass_lookup = Runtime.lookup(StaticClass.class);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static MethodHandle target_$_aaa_setter_mh;
    public static MethodHandle target_$_aaa_getter_mh;
    public static MethodHandle target_$_aaa2_getter_mh;
    public static MethodHandle target_$_obj2_getter_mh;
    public static MethodHandles.Lookup java_lang_Object_lookup;
    public MethodHandle target_$_obj2_$_aaa_getter_mh;
    public MethodHandle target_$_obj2_$_obj3_getter_mh;
    public MethodHandles.Lookup target_$_obj2_$_obj3_lookup;
    public MethodHandle target_$_obj2_$_obj3_$_aaa_getter_mh;

    public MyStaticClass$impl(StaticClass var1) {
        super(var1);
    }

    static {
  try {
      target_$_aaa_setter_mh = io_github_hhy_linker_test_statictest_StaticClass_lookup.findStaticSetter(StaticClass.class, "aaa", String.class);
      target_$_aaa_getter_mh = io_github_hhy_linker_test_statictest_StaticClass_lookup.findStaticGetter(StaticClass.class, "aaa", String.class);
      target_$_aaa2_getter_mh = io_github_hhy_linker_test_statictest_StaticClass_lookup.findGetter(StaticClass.class, "aaa2", String.class);
      target_$_obj2_getter_mh = io_github_hhy_linker_test_statictest_StaticClass_lookup.findStaticGetter(StaticClass.class, "obj2", Object.class);
  }catch (Exception e) {}
    }

    public void setA(Object var1) {
        String var2 = (String)var1;
        this.set_target_$_aaa(var2);
    }

    public void set_target_$_aaa(String var1) {
        Object var2 = this.target;
        try {
            target_$_aaa_setter_mh.invoke(var1);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String getA() {
        String var1 = this.get_target_$_aaa();
        return var1;
    }

    public String get_target_$_aaa() {
        Object var1 = this.target;
        String var2 = null;
        try {
            var2 = (String) target_$_aaa_getter_mh.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return var2;
    }

    public String getA2() {
        String var1 = this.get_target_$_aaa2();
        return var1;
    }

    public String get_target_$_aaa2() {
        Object var1 = this.target;
        if (var1 != null) {
            String var2 = null;
            try {
                var2 = (String) target_$_aaa2_getter_mh.invoke(var1);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return var2;
        } else {
            throw new NullPointerException("target[type=io.github.hhy.linker.test.statictest.StaticClass]");
        }
    }

    public String getObjAaa() {
        Object var1 = this.get_target_$_obj2_$_aaa();
        return (String)var1;
    }

    public Object get_target_$_obj2() {
        Object var1 = this.target;
        Object var2 = null;
        try {
            var2 = target_$_obj2_getter_mh.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return var2;
    }

    public Object get_target_$_obj2_$_aaa() {
        try {
            Object var1 = this.get_target_$_obj2();
            if (var1 == null) {
                java_lang_Object_lookup = Runtime.findLookup(io_github_hhy_linker_test_statictest_StaticClass_lookup.lookupClass(), "obj2");
            }

            if (java_lang_Object_lookup == null || var1.getClass() != java_lang_Object_lookup.lookupClass()) {
                java_lang_Object_lookup = Runtime.lookup(var1.getClass());
                this.target_$_obj2_$_aaa_getter_mh = Runtime.findGetter(java_lang_Object_lookup, "aaa");
            }

            if (this.target_$_obj2_$_aaa_getter_mh == null) {
                this.target_$_obj2_$_aaa_getter_mh = Runtime.findGetter(java_lang_Object_lookup, "aaa");
            }

            Object var2;
            if (!this.target_$_obj2_$_aaa_getter_mh.getClass().getName().contains("DirectMethodHandle$StaticAccessor")) {
                if (var1 == null) {
                    throw new NullPointerException("obj2[type=java.lang.Object]");
                }

                var2 = this.target_$_obj2_$_aaa_getter_mh.invoke(var1);
            } else {
                var2 = this.target_$_obj2_$_aaa_getter_mh.invoke();
            }

            return var2;
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }

    public String getObjAaa2() {
        Object var1 = this.get_target_$_obj2_$_obj3_$_aaa();
        return (String)var1;
    }

    public Object get_target_$_obj2_$_obj3() {
        try {
            Object var1 = this.get_target_$_obj2();
            if (var1 == null) {
                java_lang_Object_lookup = Runtime.findLookup(io_github_hhy_linker_test_statictest_StaticClass_lookup.lookupClass(), "obj2");
            }

            if (java_lang_Object_lookup == null || var1.getClass() != java_lang_Object_lookup.lookupClass()) {
                java_lang_Object_lookup = Runtime.lookup(var1.getClass());
                this.target_$_obj2_$_obj3_getter_mh = Runtime.findGetter(java_lang_Object_lookup, "obj3");
            }

            if (this.target_$_obj2_$_obj3_getter_mh == null) {
                this.target_$_obj2_$_obj3_getter_mh = Runtime.findGetter(java_lang_Object_lookup, "obj3");
            }

            Object var2;
            if (!this.target_$_obj2_$_obj3_getter_mh.getClass().getName().contains("DirectMethodHandle$StaticAccessor")) {
                if (var1 == null) {
                    throw new NullPointerException("obj2[type=java.lang.Object]");
                }

                var2 = this.target_$_obj2_$_obj3_getter_mh.invoke(var1);
            } else {
                var2 = this.target_$_obj2_$_obj3_getter_mh.invoke();
            }

            return var2;
        }catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Object get_target_$_obj2_$_obj3_$_aaa() {
        try {


        Object var1 = this.get_target_$_obj2_$_obj3();
        if (var1 == null) {
            this.target_$_obj2_$_obj3_lookup = Runtime.findLookup(java_lang_Object_lookup.lookupClass(), "obj3");
        }

        if (this.target_$_obj2_$_obj3_lookup == null || (var1 != null && var1.getClass() != this.target_$_obj2_$_obj3_lookup.lookupClass())) {
            this.target_$_obj2_$_obj3_lookup = Runtime.lookup(var1.getClass());
            this.target_$_obj2_$_obj3_$_aaa_getter_mh = Runtime.findGetter(this.target_$_obj2_$_obj3_lookup, "aaa");
        }

        if (this.target_$_obj2_$_obj3_$_aaa_getter_mh == null) {
            this.target_$_obj2_$_obj3_$_aaa_getter_mh = Runtime.findGetter(this.target_$_obj2_$_obj3_lookup, "aaa");
        }

        Object var2;
        if (!this.target_$_obj2_$_obj3_$_aaa_getter_mh.getClass().getName().contains("DirectMethodHandle$StaticAccessor")) {
            if (var1 == null) {
                throw new NullPointerException("obj3[type=java.lang.Object]");
            }

            var2 = this.target_$_obj2_$_obj3_$_aaa_getter_mh.invoke(var1);
        } else {
            var2 = this.target_$_obj2_$_obj3_$_aaa_getter_mh.invoke();
        }

        return var2;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
