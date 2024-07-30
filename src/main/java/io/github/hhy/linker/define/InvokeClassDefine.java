package io.github.hhy.linker.define;

import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.exceptions.VerifyException;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


@Data
public class InvokeClassDefine {
    private Class<?> define;
    private Class<?> target;
    private List<MethodDefine> methodDefines;

    public static <T> InvokeClassDefine parse(Class<T> define, Class<?> targetClass) {
        if (!define.isInterface()) {
            throw new VerifyException("class define must a be interface");
        }
        Target.Bind annotation = define.getAnnotation(Target.Bind.class);
        if (annotation == null) {
            throw new VerifyException("use @Target.Bind specified a class");
        } else if (!annotation.value().equals(targetClass.getName())) {
            throw new VerifyException("@Target.Bind specified target " + annotation.value() + ", but used another target class [" + targetClass.getName() + "]");
        }

        List<MethodDefine> methodDefines = new ArrayList<>();
        for (Method declaredMethod : define.getDeclaredMethods()) {
            methodDefines.add(MethodDefine.parseMethod(targetClass, declaredMethod));
        }
        InvokeClassDefine classDefine = new InvokeClassDefine();
        classDefine.define = define;
        classDefine.target = targetClass;
        classDefine.methodDefines = methodDefines;
        return classDefine;
    }
}
