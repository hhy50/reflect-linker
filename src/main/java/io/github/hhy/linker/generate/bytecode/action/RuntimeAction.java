package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.runtime.Runtime;

public class RuntimeAction {
    public static MethodInvokeAction findLookup(LookupMember lookup, String fieldName) {
        return new MethodInvokeAction(Runtime.FIND_LOOKUP)
                .setArgs(new MethodInvokeAction(MethodHolder.LOOKUP_LOOKUP_CLASS)
                        .setInstance(lookup), LdcLoadAction.of(fieldName));
    }


    public static MethodInvokeAction lookup(Action action) {
        return new MethodInvokeAction(Runtime.LOOKUP)
                .setArgs(action);
    }

    public static MethodInvokeAction lookupWithCl(Action clLoadAction, String className) {
        return new MethodInvokeAction(Runtime.LOOKUP2)
                .setArgs(clLoadAction, LdcLoadAction.of(className));
    }

    public static MethodInvokeAction findSetter(LookupMember lookupMember, String fieldName) {
        return new MethodInvokeAction(Runtime.FIND_SETTER)
                .setArgs(lookupMember, LdcLoadAction.of(fieldName));
    }
}
