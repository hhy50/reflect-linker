package io.github.hhy50.linker.generate.setter;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.AbsMethodDefine;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.AbstractDecorator;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;


/**
 * The type Setter decorator.
 */
public class SetterDecorator extends AbstractDecorator {

    private Setter setter;
    private final FieldRef fieldRef;

    /**
     * Instantiates a new Setter decorator.
     *
     * @param setter          the setter
     * @param fieldRef        the field ref
     * @param absMethodDefine the method define
     */
    public SetterDecorator(Setter setter, FieldRef fieldRef, AbsMethodDefine absMethodDefine) {
        super(absMethodDefine);
        this.setter = setter;
        this.fieldRef = fieldRef;
    }

    @Override
    public void define0(InvokeClassImplBuilder classImplBuilder) {
        setter.define(classImplBuilder);
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        // 方法定义的类型
        typecastArgs(methodBody, methodBody.getArgs(), new Type[]{fieldRef.getType()});
        setter.invoke(methodBody);
        AsmUtil.areturn(methodBody.getWriter(), Type.VOID_TYPE);
        return null;
    }
}
