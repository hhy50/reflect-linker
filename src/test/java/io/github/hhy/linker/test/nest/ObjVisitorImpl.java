package io.github.hhy.linker.test.nest;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy.linker.runtime.Runtime;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;


public class ObjVisitorImpl extends DefaultTargetProviderImpl implements ObjVisitor {
    private static final MethodHandles.Lookup lookup;
    private MethodHandle a_getter_mh;
    private MethodHandles.Lookup a_lookup;
    private MethodHandle a_c_getter_mh;

    static {
        try {
            lookup = Runtime.lookup(Obj.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ObjVisitorImpl(Object target) throws Throwable {
        super(target);

    }

    @Field.Getter("a")
    public Object getA() {
        try {
            if (target == null) {
                throw new NullPointerException();
            }
            if (a_getter_mh == null) {
                a_getter_mh = Runtime.findGetter(lookup, target, "a");
            }
            return a_getter_mh.invoke(target);
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
            Object a = getA();
            if (a == null) {
                throw new NullPointerException();
            }
            if (a_lookup == null || a.getClass() != a_lookup.lookupClass()) {
                a_lookup = Runtime.lookup(a.getClass());
                a_c_getter_mh = Runtime.findGetter(a_lookup, a, "c");
            }
            return a_c_getter_mh.invoke(a);
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
