package io.github.hhy50.linker.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 把指定类的静态方法/静态字段加入查找过程。
 * <p>
 * 查找顺序：优先查找 this（目标类）自身的成员，找不到时再到本注解导入的类中查找静态成员。
 * <p>
 * 可标注在接口上（对所有方法生效）或单个方法上（方法级优先于类级）。
 * <p>
 * 导入的静态方法若为 public（且参数/返回类型均为 public），将直接生成为 invokestatic 调用；
 * 否则通过 MethodHandle 特权查找调用。
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.TYPE, ElementType.METHOD})
public @interface ImportStatic {

    /**
     * 导入的单个类
     */
    Class<?> value() default void.class;

    /**
     * 导入的类名列表，每个元素内还支持用逗号分割多个类名
     */
    String[] classes() default {};
}
