package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.md.AbsInterfaceMetadata;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.bytecode.ClassTypeFieldVarInst;
import io.github.hhy50.linker.generate.bytecode.FieldVarInst;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.utils.Fields;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.bytecode.vars.VarInstWithLookup;
import io.github.hhy50.linker.generate.invoker.Getter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * The type Target field getter.
 */
public class TargetFieldGetter extends Getter {
    private final FieldVarInst targetFieldVarInst;

    private ClassTypeFieldVarInst targetClassFieldVarInst;

    /**
     *
     */
    private final Type targetType;

    private final AbsInterfaceMetadata metadata;

    /**
     * Instantiates a new Target field getter.
     *
     * @param targetFieldRef the target field ref
     * @param metadata       the metadata
     */
    public TargetFieldGetter(EarlyFieldRef targetFieldRef, AbsInterfaceMetadata metadata) {
        super(targetFieldRef);
        this.targetFieldVarInst = Fields.of(targetFieldRef.getName(), ObjectVar.TYPE);
        this.targetType = targetFieldRef.getType();
        this.metadata = metadata;
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        if (this.metadata.isRuntime()) {
            this.targetClassFieldVarInst = classImplBuilder.defineLookupClass("target");
            classImplBuilder.defineConstruct(Opcodes.ACC_PUBLIC, Object.class, Class.class)
                    .intercept(Methods.invokeSuper(MethodDescriptor.ofConstructor(Object.class)).setArgs(Args.of(0))
                            .andThen(this.targetClassFieldVarInst.store(Args.of(1)))
                            .andThen(Actions.vreturn()));
        }
    }

    @Override
    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        return ChainAction.of(() -> new VarInstWithLookup(this.targetFieldVarInst, this.targetClassFieldVarInst, this.targetType));
    }
}
