package io.github.hhy50.linker.annotations;

import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.util.StringUtil;
import io.github.hhy50.linker.util.Verifier;

import java.lang.annotation.Target;
import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * <p>Field interface.</p>
 *
 * @author hanhaiyang
 * @version $Id : $Id
 */
public interface Field {

    /**
     * <p>获取指定字段值</p>
     * 这个字段可以是 private | static, 支持获取链式字段 a.b.c
     * 这个注解方法的返回值不能是void, 并且参数的长度必须为0
     */
    @Verify.Unique
    @Verify.Custom(GetterVerifier.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface Getter {
        /**
         * Value string.
         *
         * @return the string
         */
        String value() default "";
    }

    /**
     * <p>设置指定字段值</p>
     * 这个字段可以是private | static, 但不能是 final
     * 这个注解方法的返回值类型必须为void, 并且参数的长度必须为1
     */
    @Verify.Unique
    @Verify.Custom(SetterVerifier.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface Setter {
        /**
         * Value string.
         *
         * @return the string
         */
        String value() default "";
    }

    /**
     * The type Getter verifier.
     */
    class GetterVerifier implements Verifier {
        @Override
        public boolean test(Method method, Annotation anno) {
            assert anno.annotationType() == Getter.class;
            Getter getter = (Getter) anno;
            if (StringUtil.isEmpty(getter.value())) {
                throw new VerifyException("method [" + method.getDeclaringClass() + "@" + method.getName() + "] is getter method, must be specified field-expression");
            }
            if (method.getReturnType() == void.class || method.getParameters().length > 0) {
                throw new VerifyException("method [" + method.getDeclaringClass() + "@" + method.getName() + "] is getter method,its return value cannot be of type void, and the parameter length must be 0");
            }
            return true;
        }
    }

    /**
     * The type Setter verifier.
     */
    class SetterVerifier implements Verifier {
        @Override
        public boolean test(Method method, Annotation anno) {
            assert anno.annotationType() == Setter.class;
            Setter setter = (Setter) anno;
            if (StringUtil.isEmpty(setter.value())) {
                throw new VerifyException("method [" + method.getDeclaringClass() + "@" + method.getName() + "] is getter method, must be specified field-expression");
            }
            if (method.getReturnType() != void.class || method.getParameters().length != 1) {
                throw new VerifyException("method [" + method.getDeclaringClass() + "@" + method.getName() + "] is setter method,its return value must be of type void, and the parameter length must be 1");
            }
            return true;
        }
    }
}
