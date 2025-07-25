package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.invoker.Invoker;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.List;

public class MethodExprRef extends MethodRef {

    private final Method method;
    private final List<MethodRef> methods;

    /**
     * Instantiates a new Method ref.
     *
     */
    public MethodExprRef(Method method, List<MethodRef> methods) {
        super(null, null, "expr_"+COUNTER.incrementAndGet());
        this.method = method;
        this.methods = methods;
    }

    public List<MethodRef> getMethods() {
        return methods;
    }

    @Override
    public Type getMethodType() {
        return Type.getType(method);
//        Type type = Type.getType(this.method);
//        if (isInvisible()) {
//            return genericType(type);
//        }
//        return type;
    }

    @Override
    public Invoker<?> defineInvoker() {
        return null;
    }
}
