package io.github.hhy50.linker.test.v2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class AutolinkAllPlaceholderExprTest {

    public interface PilotView {
        @Field.Getter("name")
        String getName();

        @Field.Getter("rank")
        int getRank();
    }

    public interface BadgeView {
        @Field.Getter("code")
        String getCode();

        @Autolink
        @Field.Getter("owner")
        PilotView getOwner();
    }

    public interface AutolinkAllPlaceholderLinker {

        @Autolink
        @Method.Expr("inspect(..)")
        String inspectLinkedPilot(PilotView pilot, String tag, int lane);

        @Autolink
        @Method.Expr("forge(..)")
        BadgeView forgeLinkedBadge(PilotView pilot, String tag);

        @Autolink
        @Method.Expr("promote(..)")
        PilotView promoteLinkedPilot(PilotView pilot, int delta);

        @Autolink
        @Method.Expr("record(..).reveal(..)")
        String replayLinkedPilotAcrossSteps(PilotView pilot, String tag, int lane);

        @Autolink
        @Method.Expr("spawn(..)")
        PilotView spawnLinkedPilot(String name, int rank);
    }

    public static class Pilot {
        private final String name;
        private final int rank;

        public Pilot(String name, int rank) {
            this.name = name;
            this.rank = rank;
        }
    }

    public static class Badge {
        private final String code;
        private final Pilot owner;

        public Badge(String code, Pilot owner) {
            this.code = code;
            this.owner = owner;
        }
    }

    public static class AutolinkAllPlaceholderTarget {
        private String memo;

        public String inspect(Pilot pilot, String tag, Integer lane) {
            return tag + ":" + pilot.name + ":" + lane;
        }

        public Badge forge(Pilot pilot, String tag) {
            return new Badge(tag + "-" + pilot.name, pilot);
        }

        public Pilot promote(Pilot pilot, Integer delta) {
            return new Pilot(pilot.name + "-" + delta, pilot.rank + delta);
        }

        public AutolinkAllPlaceholderTarget record(Pilot pilot, String tag, Integer lane) {
            this.memo = tag + ":" + pilot.name + ":" + lane;
            return this;
        }

        public String reveal(Pilot pilot, String tag, Integer lane) {
            return memo + "|" + tag + ":" + pilot.name + ":" + lane;
        }

        public Pilot spawn(String name, Integer rank) {
            return new Pilot(name, rank);
        }
    }

    @Test
    public void shouldForwardAutolinkArgumentWithAllPlaceholder() throws LinkerException {
        AutolinkAllPlaceholderLinker linker = LinkerFactory.createLinker(
                AutolinkAllPlaceholderLinker.class,
                new AutolinkAllPlaceholderTarget()
        );
        PilotView pilot = LinkerFactory.createLinker(PilotView.class, new Pilot("mira", 5));

        Assert.assertEquals("gate:mira:3", linker.inspectLinkedPilot(pilot, "gate", 3));

        BadgeView badge = linker.forgeLinkedBadge(pilot, "seal");
        Assert.assertEquals("seal-mira", badge.getCode());
        Assert.assertEquals("mira", badge.getOwner().getName());
        Assert.assertEquals(5, badge.getOwner().getRank());
    }

    @Test
    public void shouldAutolinkReturnAndMultiStepForwardingWithAllPlaceholder() throws LinkerException {
        AutolinkAllPlaceholderLinker linker = LinkerFactory.createLinker(
                AutolinkAllPlaceholderLinker.class,
                new AutolinkAllPlaceholderTarget()
        );
        PilotView pilot = LinkerFactory.createLinker(PilotView.class, new Pilot("mira", 5));

        PilotView promoted = linker.promoteLinkedPilot(pilot, 4);
        Assert.assertEquals("mira-4", promoted.getName());
        Assert.assertEquals(9, promoted.getRank());

        Assert.assertEquals(
                "gate:mira:3|gate:mira:3",
                linker.replayLinkedPilotAcrossSteps(pilot, "gate", 3)
        );

        PilotView spawned = linker.spawnLinkedPilot("nova", 7);
        Assert.assertEquals("nova", spawned.getName());
        Assert.assertEquals(7, spawned.getRank());
    }
}
