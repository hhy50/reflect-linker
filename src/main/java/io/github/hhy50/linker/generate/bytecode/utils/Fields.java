package io.github.hhy50.linker.generate.bytecode.utils;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.asm.AsmField;
import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.exceptions.MemberNotFoundException;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.FieldVarInst;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LazyTypedAction;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * The type Fields.
 */
public class Fields {

    /**
     * Of field.
     *
     * @param name the field name
     * @param type      the type
     * @return the field
     */
    public static FieldVarInst of(String name, Type type) {
        return new FieldVarInst(Opcodes.ACC_PUBLIC, null, name, type);
    }

    /**
     * Of static field.
     *
     * @param name the field name
     * @param type      the type
     * @return the field
     */
    public static FieldVarInst ofStatic(String name, Type type) {
        return new FieldVarInst(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, null, name, type);
    }

    /**
     * Of static field.
     *
     * @param owner     the owner
     * @param name the field name
     * @param type      the type
     * @return the field
     */
    public static FieldVarInst ofStatic(String owner, String name, Type type) {
        return new FieldVarInst(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, owner, name, type);
    }

    /**
     * Of static field.
     *
     * @param owner     the owner
     * @param name the field name
     * @return the field
     * @throws NoSuchFieldException the no such field exception
     */
    public static FieldVarInst of(Class owner, String name) throws NoSuchFieldException {
        java.lang.reflect.Field field = owner.getDeclaredField(name);
        return new FieldVarInst(AsmUtil.toAsmOpcode(field.getModifiers()), TypeUtil.toOwner(owner.getName()), name, Type.getType(field.getType()));
    }

    /**
     * Of field with store.
     *
     * @param name the field name
     * @param action    the action
     * @return the field
     */
    public static Action ofStore(String name, Action action) {
        return body -> {
            AsmClassBuilder classBuilder = body.getClassBuilder();
            AsmField field = classBuilder.getField(name);
            if (field == null) {
                throw new MemberNotFoundException(classBuilder.getClassName(), name);
            }
            FieldVarInst fieldVarInst = new FieldVarInst(field);
            body.append(fieldVarInst.store(action));
        };
    }

    /**
     * Of field on load to stack
     *
     * @param name the field name
     * @return the field
     */
    public static LazyTypedAction ofLoad(String name) {
        return new LazyTypedAction() {
            Type type;

            @Override
            public Type getType() {
                return type;
            }

            @Override
            public void apply(MethodBody body) {
                AsmClassBuilder classBuilder = body.getClassBuilder();
                AsmField field = classBuilder.getField(name);
                if (field == null) {
                    throw new MemberNotFoundException(classBuilder.getClassName(), name);
                }
                this.type = field.type;
                FieldVarInst fieldVarInst = new FieldVarInst(field);
                body.append(fieldVarInst);
            }
        };
    }
}
