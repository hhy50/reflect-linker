package io.github.hhy50.linker.generate.bytecode.utils;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.asm.AsmField;
import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.exceptions.MemberNotFoundException;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.Member;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LazyTypedAction;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;

/**
 * The type Members.
 */
public class Members {

    /**
     * Of member.
     *
     * @param memberName the member name
     * @param type       the type
     * @return the member
     */
    public static Member of(String memberName, Type type) {
        return new Member(Opcodes.ACC_PUBLIC, null, memberName, type);
    }

    /**
     * Of static member.
     *
     * @param memberName the member name
     * @param type       the type
     * @return the member
     */
    public static Member ofStatic(String memberName, Type type) {
        return new Member(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, null, memberName, type);
    }

    /**
     * Of static member.
     *
     * @param owner      the owner
     * @param memberName the member name
     * @param type       the type
     * @return the member
     */
    public static Member ofStatic(String owner, String memberName, Type type) {
        return new Member(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, owner, memberName, type);
    }

    /**
     * Of static member.
     *
     * @param owner      the owner
     * @param memberName the member name
     * @return the member
     */
    public static Member of(Class owner, String memberName) throws NoSuchFieldException {
        Field field = owner.getDeclaredField(memberName);
        return new Member(AsmUtil.toAsmOpcode(field.getModifiers()), TypeUtil.toOwner(owner.getName()), memberName, Type.getType(field.getType()));
    }

    /**
     * Of member with store.
     *
     * @param memberName the member name
     * @param action     the action
     * @return the member
     */
    public static Action ofStore(String memberName, Action action) {
        return body -> {
            AsmClassBuilder classBuilder = body.getClassBuilder();
            AsmField field = classBuilder.getField(memberName);
            if (field == null) {
                throw new MemberNotFoundException(classBuilder.getClassName(), memberName);
            }
            Member member = new Member(field);
            member.store(body, action);
        };
    }

    /**
     * Of member on load to stack
     *
     * @param memberName the member name
     * @return the member
     */
    public static LazyTypedAction ofLoad(String memberName) {
        return new LazyTypedAction() {
            Type type;

            @Override
            public Type getType() {
                return type;
            }

            @Override
            public void apply(MethodBody body) {
                AsmClassBuilder classBuilder = body.getClassBuilder();
                AsmField field = classBuilder.getField(memberName);
                if (field == null) {
                    throw new MemberNotFoundException(classBuilder.getClassName(), memberName);
                }
                this.type = field.type;
                Member member = new Member(field);
                member.load(body);
            }
        };
    }
}
