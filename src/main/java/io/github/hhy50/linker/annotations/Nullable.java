package io.github.hhy50.linker.annotations;


import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.util.Verifier;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;


public interface Nullable {

    /**
     * 当表达式因为null终止时，返回的默认值, 仅适用于基本数据类型, 所有的引用类型（包括引用类型和String）默认返回null
     * bool类型的默认值为 "false" | "true"
     *
     */
    @Verify.Custom(DefaultCheck.class)
    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target({ElementType.METHOD})
    public @interface Default {
        public String value();
    }

    static class DefaultCheck implements Verifier {
        public static final String[] boolValues = {"true", "false"};

        @Override
        public boolean test(Method method, Annotation annotation) {
            Class<?> returnType = method.getReturnType();
            if (!method.getReturnType().isPrimitive()) {
                throw new VerifyException("method ["+method.getDeclaringClass()+"@"+method.getName()+"] "+
                        "return type is not primitive, Unable to use @Nullable.Default annotation");
            }
            String value = ((Default) annotation).value();
            if (returnType == boolean.class) {
                Boolean.parseBoolean(value);
            } else if (returnType == char.class) {
                if (value.length() != 1) {
                    throw new VerifyException("method ["+method.getDeclaringClass()+"@"+method.getName()+"] "+
                            "return type is char, @Nullable.Default value must be a single character");
                }
            } else if (returnType == byte.class || returnType == short.class || returnType == int.class || returnType == long.class) {
                Long.parseLong(value);
            } else if (returnType == float.class || returnType == double.class) {
                Double.parseDouble(value);
            }
            return true;
        }
    }
}
