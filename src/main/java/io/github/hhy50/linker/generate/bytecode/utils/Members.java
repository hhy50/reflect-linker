package io.github.hhy50.linker.generate.bytecode.utils;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.exceptions.MemberNotFoundException;
import io.github.hhy50.linker.generate.bytecode.Member;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

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
        return new Member(Opcodes.ACC_PUBLIC|Opcodes.ACC_STATIC, null, memberName, type);
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
            Member member = classBuilder.getMembers().get(memberName);
            if (member == null) {
                throw new MemberNotFoundException(classBuilder.getClassName(), memberName);
            }
            member.store(body, action);
        };
    }

    /**
     * Of member on load to stack
     *
     * @param memberName the member name
     * @return the member
     */
    public static Action ofLoad(String memberName) {
        return body -> {
            AsmClassBuilder classBuilder = body.getClassBuilder();
            Member member = classBuilder.getMembers().get(memberName);
            if (member == null) {
                throw new MemberNotFoundException(classBuilder.getClassName(), memberName);
            }
            member.load(body);
        };
    }
}
