package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.invoker.Invoker;
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.IntStream;

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
        int c = method.getParameterCount();
        return Type.getMethodType(method.getReturnType() == void.class ? Type.VOID_TYPE : ObjectVar.TYPE, IntStream.range(0, c).mapToObj(i ->  ObjectVar.TYPE).toArray(Type[]::new));
//        Type type = Type.getType(this.method);
//        if (isInvisible()) {
//            return genericType(type);
//        }
//        return type;
    }

    @Override
    public Invoker<?> defineInvoker() {
        return new MethodExprInvoker(this);
    }
}
