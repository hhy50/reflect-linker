package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.invoker.DirectStaticMethodInvoker;
import io.github.hhy50.linker.util.ClassUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 通过 @ImportStatic 导入的静态方法引用。
 * <p>
 * public 的静态方法（且参数/返回类型均为 public）直接生成为 invokestatic 调用，
 * 非 public（private/default/protected）的沿用 MethodHandle 特权查找调用。
 */
public class ImportedStaticMethodRef extends EarlyMethodRef {

    /**
     * Instantiates a new imported static method ref.
     *
     * @param reflect the method
     */
    public ImportedStaticMethodRef(Method reflect) {
        super(reflect);
    }

    @Override
    public MethodHandle defineInvoker() {
        if (isDirectInvokable()) {
            return new DirectStaticMethodInvoker(getReflect());
        }
        return super.defineInvoker();
    }

    /**
     * 是否可以直接 invokestatic 调用：
     * 方法为 public static，且声明类、参数类型、返回类型均为 public
     *
     * @return the boolean
     */
    public boolean isDirectInvokable() {
        Method method = getReflect();
        return Modifier.isStatic(method.getModifiers())
                && Modifier.isPublic(method.getModifiers())
                && ClassUtil.isPublic(method.getDeclaringClass())
                && !isInvisible();
    }
}
