//package io.github.hhy.linker.test;
//
//import io.github.hhy.linker.define.DefaultTargetProviderImpl;
//import io.github.hhy.linker.util.Util;
//
//import java.lang.invoke.MethodHandle;
//import java.lang.invoke.MethodHandles;
//import java.util.ArrayList;
//
//public class MyArrayList$impl1 extends DefaultTargetProviderImpl implements MyArrayList {
//
//    private static MethodHandles.Lookup lookup;
//    private static final MethodHandle elementData_getter_mh;
//    private static final MethodHandle elementData_setter_mh;
//
//    static {
//        lookup = Util.lookup(ArrayList.class);
//        try {
//            elementData_getter_mh = lookup.findGetter(ArrayList.class, "elementData", Object[].class);
//            elementData_setter_mh = lookup.findSetter(ArrayList.class, "elementData", Object[].class);
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public MyArrayList$impl1(ArrayList var1) {
//        super(var1);
//    }
//
//    public void add(Object var1) {
//        throw new NoSuchMethodError("aa");
//    }
//
//    public Object[] getElementData() {
//        try {
//            return (Object[]) elementData_getter_mh.invoke((ArrayList)this.getTarget());
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void setElementData(Object[] var1) {
//        throw new NoSuchMethodError("aa");
//    }
//
//    public static void main(String[] args) {
//        ArrayList arrayList = new ArrayList();
//        arrayList.add("aaaa");
//        arrayList.add("bbbb");
//        MyArrayList$impl1 myArrayList$impl1 = new MyArrayList$impl1(arrayList);
//
//        Object[] elementData = myArrayList$impl1.getElementData();
//        System.out.println(elementData);
//    }
//}
