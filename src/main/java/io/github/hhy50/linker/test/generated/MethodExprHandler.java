package io.github.hhy50.linker.test.generated;


import java.lang.invoke.MethodHandle;

/**
 * a.b.c.aa(a.d, 1, '2').bb('12345)
 */
public class MethodExprHandler {

    private Object target;

    private static final MethodHandle A_GETTER = null;
    private static final MethodHandle B_GETTER = null;
    private static final MethodHandle C_GETTER = null;
    private static final MethodHandle D_GETTER = null;

    private static final MethodHandle method_aa = null;
    private static final MethodHandle method_bb = null;

    static {

    }

    public MethodExprHandler(Object target) {
        this.target = target;
    }

    public Object invoke_aa() throws Throwable {
        Object c = getC();
        method_aa.invoke(c, 1, '1');

        return null;
    }

    public Object invoke_bb() throws Throwable {
        Object c = getC();
        method_bb.invoke(getC(), "12345");
        return null;
    }

    private Object getC() {
        return null;
    }
}
