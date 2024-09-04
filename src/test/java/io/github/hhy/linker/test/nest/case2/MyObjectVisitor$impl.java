////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by FernFlower decompiler)
////
//
//package io.github.hhy.linker.test.nest.case2;
//
//import io.github.hhy.linker.LinkerFactory;
//import io.github.hhy.linker.define.provider.DefaultTargetProviderImpl;
//import io.github.hhy.linker.exceptions.LinkerException;
//import io.github.hhy.linker.runtime.Runtime;
//import io.github.hhy.linker.runtime.RuntimeUtil;
//import io.github.hhy.linker.test.MyInteger;
//
//import java.lang.invoke.MethodHandle;
//import java.lang.invoke.MethodHandles;
//import java.lang.invoke.MethodType;
//import java.lang.reflect.InvocationTargetException;
//
//public class MyObjectVisitor$impl extends DefaultTargetProviderImpl implements MyObjectVisitor {
//    public static final MethodHandles.Lookup io_github_hhy_linker_test_nest_case2_MyObject_lookup;
//
//    static {
//        try {
//            io_github_hhy_linker_test_nest_case2_MyObject_lookup = Runtime.lookup(MyObject.class);
//        } catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        } catch (InstantiationException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static final MethodHandle target_$_user_getter_mh;
//    public static final MethodHandles.Lookup io_github_hhy_linker_test_nest_case2_User_lookup;
//    public static final MethodHandle target_$_user_$$_getName;
//
//    public static final MethodHandle target_$_user_$$_toString;
//    public static final MethodHandle target_$_user_setter_mh;
//    public MethodHandles.Lookup target_$_user_runtime_lookup;
//    public MethodHandle target_$_user_$_address_getter_mh;
//    public MethodHandle target_$_user_$_address_setter_mh;
//    public MethodHandle target_$_user_$$_getName2;
//    public static final MethodHandle target_$_user_$_age_setter_mh;
//    public static final MethodHandle target_$_user_$_age_getter_mh;
//    public static final MethodHandle target_$_user_$_name_getter_mh;
//    public static final MethodHandle target_$_user_$_name_setter_mh;
//
//    public MyObjectVisitor$impl(MyObject var1) {
//        super(var1);
//    }
//
//    static {
//        try {
//            target_$_user_getter_mh = io_github_hhy_linker_test_nest_case2_MyObject_lookup.findGetter(MyObject.class, "user", User.class);
//            io_github_hhy_linker_test_nest_case2_User_lookup = Runtime.lookup(User.class);
//            target_$_user_$$_getName = io_github_hhy_linker_test_nest_case2_User_lookup.findSpecial(User.class, "getName", MethodType.methodType(String.class, new Class[0]), User.class);
//            target_$_user_$$_toString = io_github_hhy_linker_test_nest_case2_User_lookup.findSpecial(Object.class, "toString", MethodType.methodType(String.class, new Class[0]), User.class);
//            target_$_user_setter_mh = io_github_hhy_linker_test_nest_case2_MyObject_lookup.findSetter(MyObject.class, "user", User.class);
//            target_$_user_$_age_setter_mh = io_github_hhy_linker_test_nest_case2_User_lookup.findSetter(User.class, "age", Integer.TYPE);
//            target_$_user_$_age_getter_mh = io_github_hhy_linker_test_nest_case2_User_lookup.findGetter(User.class, "age", Integer.TYPE);
//            target_$_user_$_name_getter_mh = io_github_hhy_linker_test_nest_case2_User_lookup.findGetter(User.class, "name", String.class);
//            target_$_user_$_name_setter_mh = io_github_hhy_linker_test_nest_case2_User_lookup.findSetter(User.class, "name", String.class);
//        }catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public String getSuperName() {
//        String var1 = (String) this.invoke_target_$_user_$$_getName2();
//        return var1;
//    }
//
//    @Override
//    public String getName2() {
//        String var1 = (String) this.invoke_target_$_user_$$_getName2();
//        return var1;
//    }
//
//    public User get_target_$_user() {
//        Object var1 = this.target;
//        if (var1 != null) {
//            User var2 = null;
//            try {
//                var2 = (User) target_$_user_getter_mh.invoke(var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//            return var2;
//        } else {
//            throw new NullPointerException("target[type=io.github.hhy.linker.test.nest.case2.MyObject]");
//        }
//    }
//
//    public String  invoke_target_$_user_$$_getName2() {
//        try {
//            User var1 = this.get_target_$_user();
//            if (var1 == null) {
//                this.target_$_user_runtime_lookup = Runtime.findLookup(io_github_hhy_linker_test_nest_case2_MyObject_lookup.lookupClass(), "user");
//            }
//
//            if (this.target_$_user_runtime_lookup == null || var1 != null && var1.getClass() != this.target_$_user_runtime_lookup.lookupClass()) {
//                this.target_$_user_runtime_lookup = Runtime.lookup(var1.getClass());
//                this.target_$_user_$$_getName2 = Runtime.findMethod(this.target_$_user_runtime_lookup, "getName2", null, new String[0]);
//            }
//
//            if (this.target_$_user_$$_getName2 == null) {
//                this.target_$_user_$$_getName2 = Runtime.findMethod(this.target_$_user_runtime_lookup, "getName2", null, new String[0]);
//            }
//
//            String var2;
//            if (!this.target_$_user_$$_getName2.getClass().getName().contains("DirectMethodHandle$StaticAccessor")) {
//                if (var1 == null) {
//                    throw new NullPointerException("user[type=io.github.hhy.linker.test.nest.case2.User]");
//                }
//
//                var2 = (String) this.target_$_user_$$_getName2.invoke(var1);
//            } else {
//                var2 = (String) this.target_$_user_$$_getName2.invoke();
//            }
//            return var2;
//        }catch (Throwable e){
//            throw new RuntimeException(e);
//        }
//    }
//
//    public String superToString() {
//        String var1 = this.invoke_target_$_user_$$_toString();
//        return var1;
//    }
//
//    public String invoke_target_$_user_$$_toString() {
//        User var1 = this.get_target_$_user();
//        if (var1 != null) {
//            String var2 = null;
//            try {
//                var2 = (String) target_$_user_$$_toString.invoke(var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//            return var2;
//        } else {
//            throw new NullPointerException("user[type=io.github.hhy.linker.test.nest.case2.User]");
//        }
//    }
//
//    public UserVisitor getUser() {
//        User var1 = this.get_target_$_user();
//        if (var1 != null) {
//            Object var2 = null;
//            try {
//                var2 = LinkerFactory.createLinker(UserVisitor.class, var1);
//            } catch (LinkerException e) {
//                throw new RuntimeException(e);
//            }
//            return (UserVisitor)var2;
//        } else {
//            return null;
//        }
//    }
//
//    public void setUser(UserVisitor var1) {
//        User var2 = (User)((DefaultTargetProviderImpl)var1).getTarget();
//        this.set_target_$_user(var2);
//    }
//
//    public void set_target_$_user(User var1) {
//        Object var2 = this.target;
//        if (var2 != null) {
//            try {
//                target_$_user_setter_mh.invoke(var2, var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            throw new NullPointerException("target[type=io.github.hhy.linker.test.nest.case2.MyObject]");
//        }
//    }
//
//    public String getAddress() {
//        Object var1 = this.get_target_$_user_$_address();
//        if (!(var1 instanceof String)) {
//            throw new ClassCastException("type 'java.lang.Object' not cast to type 'java.lang.String'");
//        } else {
//            String var2 = (String)var1;
//            return var2;
//        }
//    }
//
//    public Object get_target_$_user_$_address() {
//        try {
//            User var1 = this.get_target_$_user();
//            if (var1 == null) {
//                this.target_$_user_runtime_lookup = Runtime.findLookup(io_github_hhy_linker_test_nest_case2_MyObject_lookup.lookupClass(), "user");
//            }
//
//            if (this.target_$_user_runtime_lookup == null || var1 != null && var1.getClass() != this.target_$_user_runtime_lookup.lookupClass()) {
//                this.target_$_user_runtime_lookup = Runtime.lookup(var1.getClass());
//                this.target_$_user_$_address_getter_mh = Runtime.findGetter(this.target_$_user_runtime_lookup, "address");
//            }
//
//            if (this.target_$_user_$_address_getter_mh == null) {
//                this.target_$_user_$_address_getter_mh = Runtime.findGetter(this.target_$_user_runtime_lookup, "address");
//            }
//
//            Object var2;
//            if (!this.target_$_user_$_address_getter_mh.getClass().getName().contains("DirectMethodHandle$StaticAccessor")) {
//                if (var1 == null) {
//                    throw new NullPointerException("user[type=io.github.hhy.linker.test.nest.case2.User]");
//                }
//
//                var2 = this.target_$_user_$_address_getter_mh.invoke(var1);
//            } else {
//                var2 = this.target_$_user_$_address_getter_mh.invoke();
//            }
//
//            return var2;
//        }catch (Throwable e){
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void setAddress(String var1) {
//        if (!(var1 instanceof Object)) {
//            throw new ClassCastException("type 'java.lang.String' not cast to type 'java.lang.Object'");
//        } else {
//            Object var2 = (Object)var1;
//            this.set_target_$_user_$_address(var2);
//        }
//    }
//
//    public void set_target_$_user_$_address(Object var1) {
//        try {
//            User var2 = this.get_target_$_user();
//            if (var2 == null) {
//                this.target_$_user_runtime_lookup = Runtime.findLookup(io_github_hhy_linker_test_nest_case2_MyObject_lookup.lookupClass(), "user");
//            }
//
//            if (this.target_$_user_runtime_lookup == null || var2 != null && var2.getClass() != this.target_$_user_runtime_lookup.lookupClass()) {
//                this.target_$_user_runtime_lookup = Runtime.lookup(var2.getClass());
//                this.target_$_user_$_address_setter_mh = Runtime.findSetter(this.target_$_user_runtime_lookup, "address");
//            }
//
//            if (this.target_$_user_$_address_setter_mh == null) {
//                this.target_$_user_$_address_setter_mh = Runtime.findSetter(this.target_$_user_runtime_lookup, "address");
//            }
//
//            if (!this.target_$_user_$_address_setter_mh.getClass().getName().contains("DirectMethodHandle$StaticAccessor")) {
//                if (var2 == null) {
//                    throw new NullPointerException("user[type=io.github.hhy.linker.test.nest.case2.User]");
//                }
//
//                this.target_$_user_$_address_setter_mh.invoke(var2, var1);
//            } else {
//                this.target_$_user_$_address_setter_mh.invoke(var1);
//            }
//        }catch (Throwable e){
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    public void setAge(MyInteger var1) {
//        Integer var2 = (Integer)((DefaultTargetProviderImpl)var1).getTarget();
//        int var3 = RuntimeUtil.unwrapInt(var2);
//        this.set_target_$_user_$_age(var3);
//    }
//
//    public void set_target_$_user_$_age(int var1) {
//        User var2 = this.get_target_$_user();
//        if (var2 != null) {
//            try {
//                target_$_user_$_age_setter_mh.invoke(var2, var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            throw new NullPointerException("user[type=io.github.hhy.linker.test.nest.case2.User]");
//        }
//    }
//
//    public MyInteger getAge() {
//        int var1 = this.get_target_$_user_$_age();
//        Integer var2 = (Integer)RuntimeUtil.wrap(var1);
//        if (var2 != null) {
//            Object var3 = null;
//            try {
//                var3 = LinkerFactory.createLinker(MyInteger.class, var2);
//            } catch (LinkerException e) {
//                throw new RuntimeException(e);
//            }
//            return (MyInteger)var3;
//        } else {
//            return null;
//        }
//    }
//
//    public int get_target_$_user_$_age() {
//        User var1 = this.get_target_$_user();
//        if (var1 != null) {
//            int var2 = 0;
//            try {
//                var2 = (int) target_$_user_$_age_getter_mh.invoke(var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//            return var2;
//        } else {
//            throw new NullPointerException("user[type=io.github.hhy.linker.test.nest.case2.User]");
//        }
//    }
//
//    public String getName() {
//        String var1 = this.get_target_$_user_$_name();
//        return var1;
//    }
//
//    public String get_target_$_user_$_name() {
//        User var1 = this.get_target_$_user();
//        if (var1 != null) {
//            String var2 = null;
//            try {
//                var2 = (String) target_$_user_$_name_getter_mh.invoke(var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//            return var2;
//        } else {
//            throw new NullPointerException("user[type=io.github.hhy.linker.test.nest.case2.User]");
//        }
//    }
//
//    public void setName(String var1) {
//        this.set_target_$_user_$_name(var1);
//    }
//
//    public void set_target_$_user_$_name(String var1) {
//        User var2 = this.get_target_$_user();
//        if (var2 != null) {
//            try {
//                target_$_user_$_name_setter_mh.invoke(var2, var1);
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            throw new NullPointerException("user[type=io.github.hhy.linker.test.nest.case2.User]");
//        }
//    }
//}
