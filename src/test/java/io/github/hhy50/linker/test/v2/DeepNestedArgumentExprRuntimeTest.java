package io.github.hhy50.linker.test.v2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class DeepNestedArgumentExprRuntimeTest {

    @Runtime
//    @Typed(name = "a.b.c.d.e.f", value = "io.github.hhy50.linker.test.v2.DeepNestedArgumentExprTest$LevelF")
    public interface DeepNestedArgumentExprRuntimeLinker {

        @Method.Expr("a.b.c.d.e.f.g.prepare().align().stream().merge().finishStage().handOff().join(a.b.c.d.e.f.g.code, a.b.c.d.e.f.g.readCode(), a.b.c.d.e.f.g.makeEnvelope($0).stamp().seal().archive().echo().finish())")
        String forwardDeepNestedArguments(String suffix);

        @Method.Expr("a.b.c.d.e.f.g.prepare().align().stream().merge().finishStage().handOff().joinMixed('anchor', $0, a.b.c.d.e.f.g.code, a.b.c.d.e.f.g.makeEnvelope($1).stamp().seal().archive().echo().finish(), 7)")
        String forwardMixedConstantParameterAndExpr(String prefix, String suffix);

        @Method.Expr("a.b.c.d.e.f.g.prepare().align().stream().merge().finishStage().handOff().joinEnvelopeMix(a.b.c.d.e.f.g.makeEnvelope('seed').stamp().seal().archive().echo().finish(), $0, a.b.c.d.e.f.g.makeEnvelope($1).stamp().seal().archive().echo().finish(), 'LOCK')")
        String forwardConstAndDynamicNestedChains(String label, String suffix);

        @Method.Expr("a.b.c.d.e.f.g.prepare().align().stream().merge().finishStage().handOff().joinSpectrum(a.b.c.d.e.f.g.readCode(), 'fixed', $0, a.b.c.d.e.f.g.makeEnvelope($1).stamp().seal().archive().echo().finish(), a.b.c.d.e.f.g.makeEnvelope('const').stamp().seal().archive().echo().finish(), 3)")
        String forwardTwoNestedChainsAndConstants(String prefix, String suffix);
    }

    @Test
    public void shouldSupportDeepNestedArgumentsWithLongChainsInRuntimeMode() throws LinkerException {
        DeepNestedArgumentExprRuntimeLinker linker = createLinker();

        Assert.assertEquals(
                "branch|branch|branch-tail#stamp#seal#archive#echo|prepare>align>stream>merge>finishStage>handOff",
                linker.forwardDeepNestedArguments("tail")
        );
    }

    @Test
    public void shouldSupportDeepMixedArgumentsWithConstantsAndParametersInRuntimeMode() throws LinkerException {
        DeepNestedArgumentExprRuntimeLinker linker = createLinker();

        Assert.assertEquals(
                "anchor|prefix|branch|branch-tail#stamp#seal#archive#echo|7|prepare>align>stream>merge>finishStage>handOff",
                linker.forwardMixedConstantParameterAndExpr("prefix", "tail")
        );
    }

    @Test
    public void shouldSupportDeepConstantAndDynamicNestedChainsInRuntimeMode() throws LinkerException {
        DeepNestedArgumentExprRuntimeLinker linker = createLinker();

        Assert.assertEquals(
                "branch-seed#stamp#seal#archive#echo|label|branch-tail#stamp#seal#archive#echo|LOCK|prepare>align>stream>merge>finishStage>handOff",
                linker.forwardConstAndDynamicNestedChains("label", "tail")
        );
    }

    @Test
    public void shouldSupportDeepMultipleNestedChainsWithConstantsInRuntimeMode() throws LinkerException {
        DeepNestedArgumentExprRuntimeLinker linker = createLinker();

        Assert.assertEquals(
                "branch|fixed|prefix|branch-tail#stamp#seal#archive#echo|branch-const#stamp#seal#archive#echo|3|prepare>align>stream>merge>finishStage>handOff",
                linker.forwardTwoNestedChainsAndConstants("prefix", "tail")
        );
    }

    private DeepNestedArgumentExprRuntimeLinker createLinker() throws LinkerException {
        return LinkerFactory.createLinker(DeepNestedArgumentExprRuntimeLinker.class, createTarget());
    }

    private DeepNestedArgumentExprTest.Root createTarget() {
        return new DeepNestedArgumentExprTest.Root(
                new DeepNestedArgumentExprTest.LevelA(
                        new DeepNestedArgumentExprTest.LevelB(
                                new DeepNestedArgumentExprTest.LevelC(
                                        new DeepNestedArgumentExprTest.LevelD(
                                                new DeepNestedArgumentExprTest.LevelE(
                                                        new DeepNestedArgumentExprTest.LevelF(
                                                                new DeepNestedArgumentExprTest.LevelG("branch")
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
