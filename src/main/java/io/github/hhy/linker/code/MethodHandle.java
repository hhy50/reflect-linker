package io.github.hhy.linker.code;

import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.code.vars.ObjectVar;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public abstract class MethodHandle {

    protected MethodHandle prevMethodHandle;

    public MethodHandle(MethodHandle prevMethodHandle) {
        this.prevMethodHandle = prevMethodHandle;
    }

    public void define(InvokeClassImplBuilder classImplBuilder) {

    }

    /**
     *
     * 调用 mh.invoke();
     * @return
     */
    public abstract ObjectVar invoke(MethodBody methodBody);


    /**
     * <pre>
     * if (lookup == null || a.getClass() != lookup.lookupClass()) {
     *      lookup = Runtime.lookup(a.getClass());
     *      mh = Runtime.findGetter(lookup, a, "field");
     * }
     * </pre>
     */
    protected void checkLookup(MethodVisitor methodVisitor) {
        methodVisitor.visitFieldInsn(Opcodes.GETFIELD, , "lookup", "Ljava/lang/invoke/MethodHandles/Lookup;");
    }
}
