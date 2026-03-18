package io.github.hhy50.linker.test.v2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class DeepNestedArgumentExprTest {

    public interface DeepNestedArgumentExprLinker {

        @Method.Expr("a.b.c.d.e.f.g.prepare().align().stream().merge().finishStage().handOff().join(a.b.c.d.e.f.g.code, a.b.c.d.e.f.g.readCode(), a.b.c.d.e.f.g.makeEnvelope($0).stamp().seal().archive().echo().finish())")
        String forwardDeepNestedArguments(String suffix);

        @Method.Expr("a.b.c.d.e.f.g.prepare().align().stream().merge().finishStage().handOff().joinMixed('anchor', $0, a.b.c.d.e.f.g.code, a.b.c.d.e.f.g.makeEnvelope($1).stamp().seal().archive().echo().finish(), 7)")
        String forwardMixedConstantParameterAndExpr(String prefix, String suffix);

        @Method.Expr("a.b.c.d.e.f.g.prepare().align().stream().merge().finishStage().handOff().joinEnvelopeMix(a.b.c.d.e.f.g.makeEnvelope('seed').stamp().seal().archive().echo().finish(), $0, a.b.c.d.e.f.g.makeEnvelope($1).stamp().seal().archive().echo().finish(), 'LOCK')")
        String forwardConstAndDynamicNestedChains(String label, String suffix);

        @Method.Expr("a.b.c.d.e.f.g.prepare().align().stream().merge().finishStage().handOff().joinSpectrum(a.b.c.d.e.f.g.readCode(), 'fixed', $0, a.b.c.d.e.f.g.makeEnvelope($1).stamp().seal().archive().echo().finish(), a.b.c.d.e.f.g.makeEnvelope('const').stamp().seal().archive().echo().finish(), 3)")
        String forwardTwoNestedChainsAndConstants(String prefix, String suffix);
    }

    public static class Root {
        private final LevelA a;

        public Root(LevelA a) {
            this.a = a;
        }
    }

    public static class LevelA {
        private final LevelB b;

        public LevelA(LevelB b) {
            this.b = b;
        }
    }

    public static class LevelB {
        private final LevelC c;

        public LevelB(LevelC c) {
            this.c = c;
        }
    }

    public static class LevelC {
        private final LevelD d;

        public LevelC(LevelD d) {
            this.d = d;
        }
    }

    public static class LevelD {
        private final LevelE e;

        public LevelD(LevelE e) {
            this.e = e;
        }
    }

    public static class LevelE {
        private final LevelF f;

        public LevelE(LevelF f) {
            this.f = f;
        }
    }

    public static class LevelF {
        private final LevelG g;

        public LevelF(LevelG g) {
            this.g = g;
        }
    }

    public static class LevelG {
        private final String code;

        public LevelG(String code) {
            this.code = code;
        }

        public Pipeline prepare() {
            return new Pipeline();
        }

        public String readCode() {
            return code;
        }

        public Envelope makeEnvelope(String suffix) {
            return new Envelope(code + "-" + suffix);
        }
    }

    public static class Pipeline {
        private final StringBuilder route = new StringBuilder("prepare");

        public Pipeline align() {
            route.append(">align");
            return this;
        }

        public Pipeline stream() {
            route.append(">stream");
            return this;
        }

        public Pipeline merge() {
            route.append(">merge");
            return this;
        }

        public Pipeline finishStage() {
            route.append(">finishStage");
            return this;
        }

        public Pipeline handOff() {
            route.append(">handOff");
            return this;
        }

        public String join(String fieldCode, String methodCode, String label) {
            return fieldCode + "|" + methodCode + "|" + label + "|" + route;
        }

        public String joinMixed(String constantTag, String prefix, String fieldCode, String label, int limit) {
            return constantTag + "|" + prefix + "|" + fieldCode + "|" + label + "|" + limit + "|" + route;
        }

        public String joinEnvelopeMix(String constantEnvelope, String label, String dynamicEnvelope, String lock) {
            return constantEnvelope + "|" + label + "|" + dynamicEnvelope + "|" + lock + "|" + route;
        }

        public String joinSpectrum(String methodCode, String constantTag, String prefix, String dynamicEnvelope,
                                   String constantEnvelope, int level) {
            return methodCode + "|" + constantTag + "|" + prefix + "|" + dynamicEnvelope + "|" + constantEnvelope
                    + "|" + level + "|" + route;
        }
    }

    public static class Envelope {
        private final StringBuilder value;

        public Envelope(String value) {
            this.value = new StringBuilder(value);
        }

        public Envelope stamp() {
            value.append("#stamp");
            return this;
        }

        public Envelope seal() {
            value.append("#seal");
            return this;
        }

        public Envelope archive() {
            value.append("#archive");
            return this;
        }

        public Envelope echo() {
            value.append("#echo");
            return this;
        }

        public String finish() {
            return value.toString();
        }
    }

    @Test
    public void shouldSupportDeepNestedArgumentsWithLongChains() throws LinkerException {
        DeepNestedArgumentExprLinker linker = createLinker();

        Assert.assertEquals(
                "branch|branch|branch-tail#stamp#seal#archive#echo|prepare>align>stream>merge>finishStage>handOff",
                linker.forwardDeepNestedArguments("tail")
        );
    }

    @Test
    public void shouldSupportDeepMixedArgumentsWithConstantsAndParameters() throws LinkerException {
        DeepNestedArgumentExprLinker linker = createLinker();

        Assert.assertEquals(
                "anchor|prefix|branch|branch-tail#stamp#seal#archive#echo|7|prepare>align>stream>merge>finishStage>handOff",
                linker.forwardMixedConstantParameterAndExpr("prefix", "tail")
        );
    }

    @Test
    public void shouldSupportDeepConstantAndDynamicNestedChains() throws LinkerException {
        DeepNestedArgumentExprLinker linker = createLinker();

        Assert.assertEquals(
                "branch-seed#stamp#seal#archive#echo|label|branch-tail#stamp#seal#archive#echo|LOCK|prepare>align>stream>merge>finishStage>handOff",
                linker.forwardConstAndDynamicNestedChains("label", "tail")
        );
    }

    @Test
    public void shouldSupportDeepMultipleNestedChainsWithConstants() throws LinkerException {
        DeepNestedArgumentExprLinker linker = createLinker();

        Assert.assertEquals(
                "branch|fixed|prefix|branch-tail#stamp#seal#archive#echo|branch-const#stamp#seal#archive#echo|3|prepare>align>stream>merge>finishStage>handOff",
                linker.forwardTwoNestedChainsAndConstants("prefix", "tail")
        );
    }

    private DeepNestedArgumentExprLinker createLinker() throws LinkerException {
        return LinkerFactory.createLinker(
                DeepNestedArgumentExprLinker.class,
                new Root(
                        new LevelA(
                                new LevelB(
                                        new LevelC(
                                                new LevelD(
                                                        new LevelE(
                                                                new LevelF(new LevelG("branch"))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
