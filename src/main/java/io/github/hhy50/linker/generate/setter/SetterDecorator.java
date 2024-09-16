package io.github.hhy50.linker.generate.setter;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.MethodDefine;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.AbstractDecorator;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;


/**
 * <p>SetterDecorator class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class SetterDecorator extends AbstractDecorator {

    private Setter setter;
    private final FieldRef fieldRef;
    private final MethodDefine methodDefine;

    /**
     * <p>Constructor for SetterDecorator.</p>
     *
     * @param setter a {@link io.github.hhy50.linker.generate.setter.Setter} object.
     * @param fieldRef a {@link io.github.hhy50.linker.define.field.FieldRef} object.
     * @param methodDefine a {@link io.github.hhy50.linker.define.MethodDefine} object.
     */
    public SetterDecorator(Setter setter, FieldRef fieldRef, MethodDefine methodDefine) {
        this.setter = setter;
        this.fieldRef = fieldRef;
        this.methodDefine = methodDefine;
    }

    /** {@inheritDoc} */
    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        setter.define(classImplBuilder);
    }

    /** {@inheritDoc} */
    @Override
    public VarInst invoke(MethodBody methodBody) {
        // 方法定义的类型
        typecastArgs(methodBody, methodBody.getArgs(), methodDefine.define.getParameterTypes(), new Type[]{fieldRef.getType()});
        setter.invoke(methodBody);
        AsmUtil.areturn(methodBody.getWriter(), Type.VOID_TYPE);
        return null;
    }
}
