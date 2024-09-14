//package io.github.hhy50.linker.test.arraylist;
//
//import io.github.hhy50.linker.annotations.Field;
//import io.github.hhy50.linker.annotations.Runtime;
//import io.github.hhy50.linker.annotations.Target;
//import io.github.hhy50.linker.define.provider.DefaultTargetProviderImpl;
//
//import java.lang.invoke.MethodHandles;
//import java.util.ArrayList;
//
///**
// * <p>MyArrayList interface.</p>
// *
// * @author hanhaiyang
// * @version $Id: $Id
// * @since 1.0.0
// */
//@Runtime
//@Target.Bind("java.util.ArrayList")
//public class MyArrayList2$impl extends DefaultTargetProviderImpl implements MyArrayList2 {
//
//    private Class target_$$_class;
//
//    /**
//     * <p>Constructor for DefaultTargetProviderImpl.</p>
//     *
//     * @param target a {@link Object} object.
//     */
//    public MyArrayList2$impl(Object target) {
//        super(target);
//    }
//
//    /**
//     * <p>getElementData.</p>
//     *
//     * @return a {@link Object} object.
//     */
//    @Field.Getter("elementData")
//    public Object getElementData() {
//        Object target1 = getTarget();
////        MethodHandles.Lookup =
//        if (target1 == null) {
//            target_$$_class = ArrayList.class;
//        }
//
//    }
//}
