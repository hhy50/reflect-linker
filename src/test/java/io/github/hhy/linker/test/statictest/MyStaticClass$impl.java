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
    public static final MethodHandles.Lookup target_lookup;

    static {
        try {
            target_lookup = Runtime.lookup(StaticClass.class);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public MethodHandle target_$_aaa_getter_mh;

    public MyStaticClass$impl(StaticClass var1) {
        super(var1);
    }

    public String getA() {
        String var1 = null;
        try {
            var1 = this.get_target_$_aaa();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return var1;
    }

    @Override
    public String getA2() {
        return null;
    }

    @Override
    public void setA(String aaa) {

    }

    public String get_target_$_aaa() throws Throwable {
        Object var1 = this.target;
        if (this.target_$_aaa_getter_mh == null) {
            MethodHandle var2 = Runtime.findGetter(target_lookup, "aaa");
            this.target_$_aaa_getter_mh = var2;
        }
        if (this.target_$_aaa_getter_mh.getClass().getName().contains("DirectMethodHandle$StaticAccessor")) {
            return (String) target_$_aaa_getter_mh.invoke();
        }
        String var3 = (String) this.target_$_aaa_getter_mh.invoke(var1);
        return var3;
    }
}
