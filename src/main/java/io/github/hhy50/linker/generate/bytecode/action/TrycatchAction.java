package io.github.hhy50.linker.generate.bytecode.action;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

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

    public TrycatchAction _catch(Type e, Action catchblock) {
        Action catchbefore = withVisitor(mv -> mv.visitLabel(cs), (Action) body -> body.newLocalVar(e, null, empty()));

        this.catchblock = catchblock;
        this.catchblock = withVisitor(catchbefore, this.catchblock, mv -> {
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
