////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package io.github.hhy.linker.test.nest;
//
//import io.github.hhy.linker.define.provider.DefaultTargetProviderImpl;
//import io.github.hhy.linker.runtime.Runtime;
//import java.lang.invoke.MethodHandle;
//import java.lang.invoke.MethodHandles;
//import java.lang.reflect.InvocationTargetException;
//
//public class ObjVisitor$impl extends DefaultTargetProviderImpl implements ObjVisitor {
//    public static final MethodHandles.Lookup io_github_hhy_linker_test_nest_Obj_lookup;
//
//    static {
//        try {
//            io_github_hhy_linker_test_nest_Obj_lookup = Runtime.lookup(Obj.class);
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        } catch (InstantiationException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static MethodHandle target_$_a_getter_mh;
//    public static MethodHandle target_$_a_setter_mh;
//    public static MethodHandles.Lookup io_github_hhy_linker_test_nest_A_lookup;
//    public static MethodHandle target_$_a_$_b_getter_mh;
//    public static MethodHandle target_$_a_$_b_setter_mh;
//    public static MethodHandles.Lookup io_github_hhy_linker_test_nest_B_lookup;
//    public static MethodHandle target_$_a_$_b_$_c_getter_mh;
//    public static MethodHandle target_$_a_$_b_$_c_setter_mh;
//    public static MethodHandles.Lookup io_github_hhy_linker_test_nest_C_lookup;
//    public static MethodHandle target_$_a_$_b_$_c_$_str_setter_mh;
//    public static MethodHandle target_$_a_$_b_$_c_$_str_getter_mh;
//    public static MethodHandles.Lookup io_github_hhy_linker_test_nest_A2_lookup;
//    public static MethodHandle target_$_a_$_c_getter_mh;
//    public static MethodHandle target_$_a_$_c_setter_mh;
//    public static MethodHandle target_$_a_$_c_$_str_getter_mh;
//    public static MethodHandle target_$_a_$_c_$_str_setter_mh;
//
//    public ObjVisitor$impl(Obj var1) {
//        super(var1);
//    }
//
//    static {
//        try {
//            target_$_a_getter_mh = io_github_hhy_linker_test_nest_Obj_lookup.findGetter(Obj.class, "a", A.class);
//            target_$_a_setter_mh = io_github_hhy_linker_test_nest_Obj_lookup.findSetter(Obj.class, "a", A.class);
//            io_github_hhy_linker_test_nest_A_lookup = Runtime.lookup(A.class);
//            target_$_a_$_b_getter_mh = io_github_hhy_linker_test_nest_A_lookup.findGetter(A.class, "b", B.class);
//            target_$_a_$_b_setter_mh = io_github_hhy_linker_test_nest_A_lookup.findSetter(A.class, "b", B.class);
//            io_github_hhy_linker_test_nest_B_lookup = Runtime.lookup(B.class);
//            target_$_a_$_b_$_c_getter_mh = io_github_hhy_linker_test_nest_B_lookup.findGetter(B.class, "c", C.class);
//            target_$_a_$_b_$_c_setter_mh = io_github_hhy_linker_test_nest_B_lookup.findSetter(B.class, "c", C.class);
//            io_github_hhy_linker_test_nest_C_lookup = Runtime.lookup(C.class);
//            target_$_a_$_b_$_c_$_str_setter_mh = io_github_hhy_linker_test_nest_C_lookup.findSetter(C.class, "str", String.class);
//            target_$_a_$_b_$_c_$_str_getter_mh = io_github_hhy_linker_test_nest_C_lookup.findGetter(C.class, "str", String.class);
//            io_github_hhy_linker_test_nest_A2_lookup = Runtime.lookup(A2.class);
//            target_$_a_$_c_getter_mh = io_github_hhy_linker_test_nest_A2_lookup.findGetter(A2.class, "c", C.class);
//            target_$_a_$_c_setter_mh = io_github_hhy_linker_test_nest_A_lookup.findSetter(A.class, "c", C.class);
//            target_$_a_$_c_$_str_setter_mh = io_github_hhy_linker_test_nest_C_lookup.findSetter(C.class, "str", String.class);
//            target_$_a_$_c_$_str_getter_mh = io_github_hhy_linker_test_nest_C_lookup.findGetter(C.class, "str", String.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Object getA() {
//        A var1 = this.get_target_$_a();
//        if (var1 == null) {
//            throw new NullPointerException("target_$_a");
//        } else {
//            return (Object)var1;
//        }
//    }
//
//    public A get_target_$_a() {
//        Object var1 = this.target;
//        if (var1 == null) {
//            throw new NullPointerException("target[type=io.github.hhy.linker.test.nest.Obj]");
//        } else {
//            A var2 = null;
//            try {
//                var2 = (A) target_$_a_getter_mh.invoke(var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//            return var2;
//        }
//    }
//
//    public void setA(Object var1) {
//        A var2 = (A)var1;
//        this.set_target_$_a(var2);
//    }
//
//    public void set_target_$_a(A var1) {
//        Object var2 = this.target;
//        if (var2 == null) {
//            throw new NullPointerException("target[type=io.github.hhy.linker.test.nest.Obj]");
//        } else {
//            try {
//                target_$_a_setter_mh.invoke(var2, var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    public Object getB() {
//        B var1 = this.get_target_$_a_$_b();
//        if (var1 == null) {
//            throw new NullPointerException("target_$_a_$_b");
//        } else {
//            return (Object)var1;
//        }
//    }
//
//    public B get_target_$_a_$_b() {
//        A var1 = this.get_target_$_a();
//        if (var1 == null) {
//            throw new NullPointerException("a[type=io.github.hhy.linker.test.nest.A]");
//        } else {
//            B var2 = null;
//            try {
//                var2 = (B) target_$_a_$_b_getter_mh.invoke(var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//            return var2;
//        }
//    }
//
//    public void setB(Object var1) {
//        B var2 = (B)var1;
//        this.set_target_$_a_$_b(var2);
//    }
//
//    public void set_target_$_a_$_b(B var1) {
//        A var2 = this.get_target_$_a();
//        if (var2 == null) {
//            throw new NullPointerException("a[type=io.github.hhy.linker.test.nest.A]");
//        } else {
//            try {
//                target_$_a_$_b_setter_mh.invoke(var2, var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    public Object getC() {
//        C var1 = this.get_target_$_a_$_b_$_c();
//        if (var1 == null) {
//            throw new NullPointerException("target_$_a_$_b_$_c");
//        } else {
//            return (Object)var1;
//        }
//    }
//
//    public C get_target_$_a_$_b_$_c() {
//        B var1 = this.get_target_$_a_$_b();
//        if (var1 == null) {
//            throw new NullPointerException("b[type=io.github.hhy.linker.test.nest.B]");
//        } else {
//            C var2 = null;
//            try {
//                var2 = (C) target_$_a_$_b_$_c_getter_mh.invoke(var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//            return var2;
//        }
//    }
//
//    public void setC(Object var1) {
//        C var2 = (C)var1;
//        this.set_target_$_a_$_b_$_c(var2);
//    }
//
//    public void set_target_$_a_$_b_$_c(C var1) {
//        B var2 = this.get_target_$_a_$_b();
//        if (var2 == null) {
//            throw new NullPointerException("b[type=io.github.hhy.linker.test.nest.B]");
//        } else {
//            try {
//                target_$_a_$_b_$_c_setter_mh.invoke(var2, var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    public void setStr(Object var1) {
//        String var2 = (String)var1;
//        this.set_target_$_a_$_b_$_c_$_str(var2);
//    }
//
//    public void set_target_$_a_$_b_$_c_$_str(String var1) {
//        C var2 = this.get_target_$_a_$_b_$_c();
//        if (var2 == null) {
//            throw new NullPointerException("c[type=io.github.hhy.linker.test.nest.C]");
//        } else {
//            try {
//                target_$_a_$_b_$_c_$_str_setter_mh.invoke(var2, var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    public String getStr() {
//        String var1 = this.get_target_$_a_$_b_$_c_$_str();
//        if (var1 == null) {
//            throw new NullPointerException("target_$_a_$_b_$_c_$_str");
//        } else {
//            return var1;
//        }
//    }
//
//    public String get_target_$_a_$_b_$_c_$_str() {
//        C var1 = this.get_target_$_a_$_b_$_c();
//        if (var1 == null) {
//            throw new NullPointerException("c[type=io.github.hhy.linker.test.nest.C]");
//        } else {
//            String var2 = null;
//            try {
//                var2 = (String) target_$_a_$_b_$_c_$_str_getter_mh.invoke(var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//            return var2;
//        }
//    }
//
//    public Object getC2() {
//        C var1 = this.get_target_$_a_$_c();
//        if (var1 == null) {
//            throw new NullPointerException("target_$_a_$_c");
//        } else {
//            return (Object)var1;
//        }
//    }
//
//    public C get_target_$_a_$_c() {
//        A var1 = this.get_target_$_a();
//        if (var1 == null) {
//            throw new NullPointerException("a[type=io.github.hhy.linker.test.nest.A]");
//        } else {
//            C var2 = null;
//            try {
//                var2 = (C) target_$_a_$_c_getter_mh.invoke(var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//            return var2;
//        }
//    }
//
//    public void setC2(Object var1) {
//        C var2 = (C)var1;
//        this.set_target_$_a_$_c(var2);
//    }
//
//    public void set_target_$_a_$_c(C var1) {
//        A var2 = this.get_target_$_a();
//        if (var2 == null) {
//            throw new NullPointerException("a[type=io.github.hhy.linker.test.nest.A]");
//        } else {
//            try {
//                target_$_a_$_c_setter_mh.invoke(var2, var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//    public String getStr2() {
//        String var1 = this.get_target_$_a_$_c_$_str();
//        if (var1 == null) {
//            throw new NullPointerException("target_$_a_$_c_$_str");
//        } else {
//            return var1;
//        }
//    }
//
//    public String get_target_$_a_$_c_$_str() {
//        C var1 = this.get_target_$_a_$_c();
//        if (var1 == null) {
//            throw new NullPointerException("c[type=io.github.hhy.linker.test.nest.C]");
//        } else {
//            String var2 = null;
//            try {
//                var2 = (String) target_$_a_$_c_$_str_getter_mh.invoke(var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//            return var2;
//        }
//    }
//
//    public void setStr2(String var1) {
//        this.set_target_$_a_$_c_$_str(var1);
//    }
//
//    public void set_target_$_a_$_c_$_str(String var1) {
//        C var2 = this.get_target_$_a_$_c();
//        if (var2 == null) {
//            throw new NullPointerException("c[type=io.github.hhy.linker.test.nest.C]");
//        } else {
//            try {
//                target_$_a_$_c_$_str_setter_mh.invoke(var2, var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//}
