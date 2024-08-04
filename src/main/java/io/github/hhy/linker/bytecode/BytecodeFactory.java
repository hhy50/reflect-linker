package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.define.TargetField;
import io.github.hhy.linker.define.TargetMethod;
import io.github.hhy.linker.define.TargetPoint;
import io.github.hhy.linker.enums.TargetPointType;

public class BytecodeFactory {

    public static BytecodeGenerator getCodeGenerator(TargetPointType type, TargetPoint targetPoint) {
        switch (type) {
            case GETTER:
                return new GetterBytecodeGenerator((TargetField) targetPoint);
            case SETTER:
                return new SetterBytecodeGenerator((TargetField) targetPoint);
            case METHOD:
                return new InvokeBytecodeGenerator((TargetMethod) targetPoint);
        }
        return null;
    }
}
