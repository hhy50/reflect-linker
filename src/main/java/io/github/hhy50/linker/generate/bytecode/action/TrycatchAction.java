package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.function.Function;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.empty;
import static io.github.hhy50.linker.generate.bytecode.action.Actions.withVisitor;

public class TrycatchAction implements LoadAction {

    Label ts = new Label();
    Label te = new Label();
    Label cs = new Label();
    Label ce = new Label();

    private Action tryblock;

    private Action catchblock;

    public TrycatchAction(Action tryblock) {
        Action trybefore = withVisitor(mv -> mv.visitLabel(ts));

        this.tryblock = tryblock;
        this.tryblock = withVisitor(trybefore, this.tryblock, mv -> {
            mv.visitLabel(te);
            mv.visitJumpInsn(Opcodes.GOTO, ce);
        });
    }

    public TrycatchAction _catch(Type e, Function<VarInst, Action> catchblock) {
        this.catchblock = withVisitor(mv -> mv.visitLabel(cs), (Action) body -> {
            body.append(catchblock.apply(body.newLocalVar(e, null, empty())));
        });
        this.catchblock = withVisitor(this.catchblock, this.catchblock, mv -> {
            mv.visitLabel(ce);
            mv.visitTryCatchBlock(ts, te, cs, e.getInternalName());
        });
        return this;
    }

    @Override
    public Action load() {
        return tryblock.andThen(catchblock);
    }
}
