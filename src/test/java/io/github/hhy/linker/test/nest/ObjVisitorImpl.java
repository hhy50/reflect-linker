package io.github.hhy.linker.test.nest;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.define.DefaultTargetProviderImpl;
import io.github.hhy.linker.runtime.Runtime;
import io.github.hhy.linker.util.Util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;


public class ObjVisitorImpl extends DefaultTargetProviderImpl implements ObjVisitor {
    private static final MethodHandles.Lookup lookup;
    private final MethodHandles.Lookup lookup2;
    private static final MethodHandle a_getter_mh;
    private final MethodHandle a_c_getter_mh;

    static {
        try {
            lookup = Runtime.lookup(Obj.class);
            a_getter_mh = lookup.findGetter(Obj.class, "a", A.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ObjVisitorImpl(Object target) throws Throwable {
        super(target);
        Object a = a_getter_mh.invoke(target);

        lookup2 = Runtime.lookup(a.getClass());
        a_c_getter_mh = lookup2.findGetter(a.getClass(), "c", Runtime.getFieldType(a, "c"));
    }

    @Field.Getter("a")
    public Object getA() {
        try {
            return a_getter_mh.invoke(getTarget());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Field.Getter("a.b")
    public Object getB() {
        return null;
    }

    @Field.Getter("a.b.c")
    public Object getC() {
        return null;
    }

    @Override
    public Object getC2() {
        try {
            return a_c_getter_mh.invoke(a_getter_mh.invoke(getTarget()));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Field.Getter("a.b.c.str")
    public String getStr() {
        return null;
    }

    public static void main(String[] args) throws Throwable {
        ObjVisitorImpl objVisitor = new ObjVisitorImpl(new Obj());
        System.out.println(objVisitor.getC2());

    }
}
