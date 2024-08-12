//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.hhy.linker.test.nest;

import io.github.hhy.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy.linker.runtime.Runtime;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;

public class ObjVisitor$impl extends DefaultTargetProviderImpl implements ObjVisitor {
    public static final MethodHandles.Lookup target_lookup;

    static {
        try {
            target_lookup = Runtime.lookup(Obj.class);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public MethodHandle target_$_a_getter_mh;
    public MethodHandles.Lookup target_$_a_lookup;
    public MethodHandle target_$_a_$_b_setter_mh;
    public MethodHandle target_$_a_$_b_getter_mh;
    public MethodHandles.Lookup target_$_a_$_b_lookup;
    public MethodHandle target_$_a_$_b_$_c_getter_mh;
    public MethodHandles.Lookup target_$_a_$_b_$_c_lookup;
    public MethodHandle target_$_a_$_b_$_c_$_str_getter_mh;
    public MethodHandle target_$_a_$_c_getter_mh;

    public ObjVisitor$impl(Obj var1) {
        super(var1);
    }

    public void setB(Object var1) {
        try {
            this.set_target_$_a_$_b(var1);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Object get_target_$_a() throws Throwable {
        Object var1 = this.target;
        if (var1 == null) {
//            if (this.target_$_a_getter_mh == null) {
//                this.target_$_a_getter_mh = Runtime.findGetter(target_lookup, target_lookup.lookupClass(), "a");
//            }
            throw new NullPointerException("null.a");
        } else {
            if (this.target_$_a_getter_mh == null) {
                MethodHandle var2 = Runtime.findGetter(target_lookup, var1, "a");
                this.target_$_a_getter_mh = var2;
            }

            Object var3 = this.target_$_a_getter_mh.invoke(var1);
            return var3;
        }
    }

    public void set_target_$_a_$_b(Object var1) throws Throwable {
        Object var2 = this.get_target_$_a();
        if (var2 == null) {
            throw new NullPointerException("a[null].b");
        } else {
            if (this.target_$_a_lookup == null || var2.getClass() != this.target_$_a_lookup.lookupClass()) {
                MethodHandles.Lookup var3 = Runtime.lookup(var2.getClass());
                this.target_$_a_lookup = var3;
                MethodHandle var4 = Runtime.findSetter(this.target_$_a_lookup, var2, "b");
                this.target_$_a_$_b_setter_mh = var4;
            }

            if (this.target_$_a_$_b_setter_mh == null) {
                MethodHandle var5 = Runtime.findSetter(this.target_$_a_lookup, var2, "b");
                this.target_$_a_$_b_setter_mh = var5;
            }

            this.target_$_a_$_b_setter_mh.invoke(var2, var1);
        }
    }

    public String getStr() {
        String var1 = null;
        try {
            var1 = this.get_target_$_a_$_b_$_c_$_str();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return var1;
    }

    public Object get_target_$_a_$_b() throws Throwable {
        Object var1 = this.get_target_$_a();
        if (var1 == null) {
            throw new NullPointerException("a[null].b");
        } else {
            if (this.target_$_a_lookup == null || var1.getClass() != this.target_$_a_lookup.lookupClass()) {
                MethodHandles.Lookup var2 = Runtime.lookup(var1.getClass());
                this.target_$_a_lookup = var2;
                MethodHandle var3 = Runtime.findGetter(this.target_$_a_lookup, var1, "b");
                this.target_$_a_$_b_getter_mh = var3;
            }

            if (this.target_$_a_$_b_getter_mh == null) {
                MethodHandle var4 = Runtime.findGetter(this.target_$_a_lookup, var1, "b");
                this.target_$_a_$_b_getter_mh = var4;
            }

            Object var5 = this.target_$_a_$_b_getter_mh.invoke(var1);
            return var5;
        }
    }

    public Object get_target_$_a_$_b_$_c()throws Throwable {
        Object var1 = this.get_target_$_a_$_b();
        if (var1 == null) {
            throw new NullPointerException("b[null].c");
        } else {
            if (this.target_$_a_$_b_lookup == null || var1.getClass() != this.target_$_a_$_b_lookup.lookupClass()) {
                MethodHandles.Lookup var2 = Runtime.lookup(var1.getClass());
                this.target_$_a_$_b_lookup = var2;
                MethodHandle var3 = Runtime.findGetter(this.target_$_a_$_b_lookup, var1, "c");
                this.target_$_a_$_b_$_c_getter_mh = var3;
            }

            if (this.target_$_a_$_b_$_c_getter_mh == null) {
                MethodHandle var4 = Runtime.findGetter(this.target_$_a_$_b_lookup, var1, "c");
                this.target_$_a_$_b_$_c_getter_mh = var4;
            }

            Object var5 = this.target_$_a_$_b_$_c_getter_mh.invoke(var1);
            return var5;
        }
    }

    public String get_target_$_a_$_b_$_c_$_str()throws Throwable {
        Object var1 = this.get_target_$_a_$_b_$_c();
        if (var1 == null) {
            throw new NullPointerException("c[null].str");
        } else {
            if (this.target_$_a_$_b_$_c_lookup == null || var1.getClass() != this.target_$_a_$_b_$_c_lookup.lookupClass()) {
                MethodHandles.Lookup var2 = Runtime.lookup(var1.getClass());
                this.target_$_a_$_b_$_c_lookup = var2;
                MethodHandle var3 = Runtime.findGetter(this.target_$_a_$_b_$_c_lookup, var1, "str");
                this.target_$_a_$_b_$_c_$_str_getter_mh = var3;
            }

            if (this.target_$_a_$_b_$_c_$_str_getter_mh == null) {
                MethodHandle var4 = Runtime.findGetter(this.target_$_a_$_b_$_c_lookup, var1, "str");
                this.target_$_a_$_b_$_c_$_str_getter_mh = var4;
            }

            String var5 = (String) this.target_$_a_$_b_$_c_$_str_getter_mh.invoke(var1);
            return var5;
        }
    }

    public Object getC2() {
        Object var1 = null;
        try {
            var1 = this.get_target_$_a_$_c();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return var1;
    }

    public Object get_target_$_a_$_c() throws Throwable{
        Object var1 = this.get_target_$_a();
        if (var1 == null) {
            throw new NullPointerException("a[null].c");
        } else {
            if (this.target_$_a_lookup == null || var1.getClass() != this.target_$_a_lookup.lookupClass()) {
                MethodHandles.Lookup var2 = Runtime.lookup(var1.getClass());
                this.target_$_a_lookup = var2;
                MethodHandle var3 = Runtime.findGetter(this.target_$_a_lookup, var1, "c");
                this.target_$_a_$_c_getter_mh = var3;
            }

            if (this.target_$_a_$_c_getter_mh == null) {
                MethodHandle var4 = Runtime.findGetter(this.target_$_a_lookup, var1, "c");
                this.target_$_a_$_c_getter_mh = var4;
            }

            Object var5 = this.target_$_a_$_c_getter_mh.invoke(var1);
            return var5;
        }
    }

    public Object getC() {
        Object var1 = null;
        try {
            var1 = this.get_target_$_a_$_b_$_c();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return var1;
    }

    public Object getB() {
        Object var1 = null;
        try {
            var1 = this.get_target_$_a_$_b();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return var1;
    }

    public Object getA() {
        Object var1 = null;
        try {
            var1 = this.get_target_$_a();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return var1;
    }
}
