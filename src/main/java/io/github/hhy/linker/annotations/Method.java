package io.github.hhy.linker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Method {

    /**
     * 指定调用的方法，默认为方法名
     */
    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target({ElementType.METHOD})
    @interface Name {
        String value();
    }

//    @Retention(RetentionPolicy.RUNTIME)
//    @java.lang.annotation.Target({ElementType.METHOD})
//    @interface DynamicSign {
//        String value();
//    }

    /**
     * 调用super
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface InvokeSuper {
        String value() default "";
    }
}
