package io.github.hhy50.linker.annotations;

import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.util.Verifier;

import java.lang.annotation.*;
import java.lang.annotation.Target;

/**
 * <p>Method interface.</p>
 *
 * @author hanhaiyang
 * @version $Id : $Id
 */
public interface Method {

    /**
     * The interface Expr.
     */
    @Verify.Unique
    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target({ElementType.METHOD})
    @interface Expr {
        /**
         * Value string.
         *
         * @return the string
         */
        String value();
    }

    /**
     * 调用super
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface InvokeSuper {
        /**
         * Value string.
         *
         * @return the string
         */
        String value() default "";
    }


    /**
     * The interface Constructor.
     */
    @Verify.Unique
    @Verify.Custom(ConstructorVerifier.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface Constructor {
    }

    /**
     * The type Constructor verifier.
     */
    class ConstructorVerifier implements Verifier {
        @Override
        public boolean test(java.lang.reflect.Method method, Annotation annotation) {
            assert annotation.annotationType() == Constructor.class;
            boolean runtime = method.getDeclaringClass().getDeclaredAnnotation(Runtime.class) != null;
            if (runtime)
                throw new VerifyException("method [" + method.getDeclaringClass() + "@" + method.getName() + "], @Constructor cannot be used with @Runtime");
            if (method.getReturnType() != method.getDeclaringClass()) {
                throw new VerifyException("method [" + method.getDeclaringClass() + "@" + method.getName() + "], return type must be this class");
            }
            return true;
        }
    }
}
