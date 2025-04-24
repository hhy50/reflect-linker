package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import org.objectweb.asm.Type;

/**
 * The type Unwrap type action.
 */
public class UnBoxAction implements LoadAction {
    private final LoadAction obj;
    private final Type primitiveType;

    /**
     * Instantiates a new Unwrap type action.
     *
     * @param obj           the obj
     * @param primitiveType the primitive type
     */
    public UnBoxAction(LoadAction obj, Type primitiveType) {
        this.obj = obj;
        this.primitiveType = primitiveType;
    }

    @Override
    public void load(MethodBody body) {
        obj.apply(body);

        Type lprimitiveType = primitiveType;
        if (lprimitiveType == null) {
            lprimitiveType = obj.getType();
        }
        MethodDescriptor md = null;
        switch (lprimitiveType.getSort()) {
            case Type.BYTE:
                md = new MethodDescriptor(RuntimeUtil.OWNER, "unwrapByte", RuntimeUtil.UNWRAP_BYTE_DESC);
                break;
            case Type.SHORT:
                md = new MethodDescriptor(RuntimeUtil.OWNER, "unwrapShort", RuntimeUtil.UNWRAP_SHORT_DESC);
                break;
            case Type.INT:
                md = new MethodDescriptor(RuntimeUtil.OWNER, "unwrapInt", RuntimeUtil.UNWRAP_INT_DESC);
                break;
            case Type.LONG:
                md = new MethodDescriptor(RuntimeUtil.OWNER, "unwrapLong", RuntimeUtil.UNWRAP_LONG_DESC);
                break;
            case Type.FLOAT:
                md = new MethodDescriptor(RuntimeUtil.OWNER, "unwrapFloat", RuntimeUtil.UNWRAP_FLOAT_DESC);
                break;
            case Type.DOUBLE:
                md = new MethodDescriptor(RuntimeUtil.OWNER, "unwrapDouble", RuntimeUtil.UNWRAP_DOUBLE_DESC);
                break;
            case Type.CHAR:
                md = new MethodDescriptor(RuntimeUtil.OWNER, "unwrapChar", RuntimeUtil.UNWRAP_CHAR_DESC);
                break;
            case Type.BOOLEAN:
                md = new MethodDescriptor(RuntimeUtil.OWNER, "unwrapBool", RuntimeUtil.UNWRAP_BOOL_DESC);
                break;
            default:
                break;
        }
        if (md != null) {
            body.append(new MethodInvokeAction(md));
        }
    }

    @Override
    public Type getType() {
        return this.primitiveType;
    }
}
