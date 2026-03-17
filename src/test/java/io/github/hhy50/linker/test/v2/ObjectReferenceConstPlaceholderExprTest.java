package io.github.hhy50.linker.test.v2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ObjectReferenceConstPlaceholderExprTest {

    public interface ObjectReferenceConstPlaceholderLinker {

        @Method.Expr("registry()?.openShelf('aurora.core')?.unwrap()")
        ShelfValue readFixedShelfFromWrappedResult();

        @Method.Expr("registry()?.openShelf('aurora.core')?.unwrapTyped()")
        Object readFixedShelfAsObjectFromTypedResult();

        @Method.Expr("registry()?.openShelf('aurora.core')?.unwrap()?.compose($0, 11, 'frozen')")
        String composeFixedShelfWithPlaceholderAndConstants(String label);

        @Method.Expr("registry()?.openShelf($0)?.unwrap()?.compose($1, 4, 'spiral')")
        String composeSelectedShelfWithPlaceholderAndConstants(String shelfName, String label);

        @Method.Expr("registryAlias()?.openShelf('aurora.core')?.unwrap()?.compose($0, 6, 'mirror')")
        String composeFromObjectRegistryWithConstantsAndPlaceholder(String label);

        @Method.Expr("registry()?.openShelf($0)?.unwrap()?.route($1)?.take('node')")
        UserValue readNodeAsUserFromWrappedResult(String shelfName, int lane);

        @Method.Expr("registry()?.openShelf($0)?.unwrapTyped()?.keeper()")
        Object readKeeperAsObjectFromTypedResult(String shelfName);

        @Method.Expr("registry()?.openShelf('void.zone')?.unwrap()?.compose($0, 2, 'empty')")
        String composeUnknownShelf(String label);

        @Method.Expr("registry()?.openShelf('aurora.core')?.unwrap()?.route(77)?.take('node')")
        UserValue readUnknownNodeAsUser();
    }

    public static class UserValue {
        private final String name;

        public UserValue(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class ShelfLane {
        private final Map<String, Object> values = new HashMap<String, Object>();

        public Object take(String key) {
            return values.get(key);
        }
    }

    public static class ShelfValue {
        private final String code;
        private final UserValue keeper;
        private final Map<Integer, ShelfLane> lanes = new HashMap<Integer, ShelfLane>();

        public ShelfValue(String code, UserValue keeper) {
            this.code = code;
            this.keeper = keeper;
        }

        public String getCode() {
            return code;
        }

        public String compose(String label, int version, String style) {
            return label + ":" + code + ":" + version + ":" + style;
        }

        public ShelfLane route(int lane) {
            return lanes.get(lane);
        }

        public UserValue keeper() {
            return keeper;
        }
    }

    public static class ShelfSlot {
        private final ShelfValue shelf;

        public ShelfSlot(ShelfValue shelf) {
            this.shelf = shelf;
        }

        public Object unwrap() {
            return shelf;
        }

        public ShelfValue unwrapTyped() {
            return shelf;
        }
    }

    public static class ShelfRegistry {
        private final Map<String, ShelfSlot> shelves = new HashMap<String, ShelfSlot>();

        public ShelfSlot openShelf(String name) {
            return shelves.get(name);
        }
    }

    public static class ObjectReferenceExprTarget {
        private ShelfRegistry registry;

        public ShelfRegistry registry() {
            return registry;
        }

        public Object registryAlias() {
            return registry;
        }
    }

    @Test
    public void shouldHandleConstAndPlaceholderArgumentsInsideNullableReferenceExpression() throws LinkerException {
        ObjectReferenceExprTarget target = createTarget();
        ObjectReferenceConstPlaceholderLinker linker = LinkerFactory.createLinker(
                ObjectReferenceConstPlaceholderLinker.class,
                target
        );

        Assert.assertEquals("aurora.core", linker.readFixedShelfFromWrappedResult().getCode());

        Object shelfAsObject = linker.readFixedShelfAsObjectFromTypedResult();
        Assert.assertTrue(shelfAsObject instanceof ShelfValue);
        Assert.assertEquals("aurora.core", ((ShelfValue) shelfAsObject).getCode());

        Assert.assertEquals(
                "lead:aurora.core:11:frozen",
                linker.composeFixedShelfWithPlaceholderAndConstants("lead")
        );
        Assert.assertEquals(
                "crest:terra.base:4:spiral",
                linker.composeSelectedShelfWithPlaceholderAndConstants("terra.base", "crest")
        );
        Assert.assertEquals(
                "halo:aurora.core:6:mirror",
                linker.composeFromObjectRegistryWithConstantsAndPlaceholder("halo")
        );

        Assert.assertEquals("iris", linker.readNodeAsUserFromWrappedResult("aurora.core", 2).getName());

        Object keeperAsObject = linker.readKeeperAsObjectFromTypedResult("aurora.core");
        Assert.assertTrue(keeperAsObject instanceof UserValue);
        Assert.assertEquals("orbit-team", ((UserValue) keeperAsObject).getName());
    }

    @Test
    public void shouldReturnNullWhenNullableReferenceExpressionStopsEarly() throws LinkerException {
        ObjectReferenceExprTarget target = new ObjectReferenceExprTarget();
        ObjectReferenceConstPlaceholderLinker linker = LinkerFactory.createLinker(
                ObjectReferenceConstPlaceholderLinker.class,
                target
        );

        Assert.assertNull(linker.readFixedShelfFromWrappedResult());
        Assert.assertNull(linker.readFixedShelfAsObjectFromTypedResult());
        Assert.assertNull(linker.composeFixedShelfWithPlaceholderAndConstants("lead"));
        Assert.assertNull(linker.composeSelectedShelfWithPlaceholderAndConstants("terra.base", "crest"));
        Assert.assertNull(linker.composeFromObjectRegistryWithConstantsAndPlaceholder("halo"));
        Assert.assertNull(linker.readNodeAsUserFromWrappedResult("aurora.core", 2));
        Assert.assertNull(linker.readKeeperAsObjectFromTypedResult("aurora.core"));

        target.registry = new ShelfRegistry();

        Assert.assertNull(linker.composeUnknownShelf("blank"));
        Assert.assertNull(linker.readUnknownNodeAsUser());

        target.registry.shelves.put("aurora.core", new ShelfSlot(new ShelfValue("aurora.core", new UserValue("orbit-team"))));
        Assert.assertNull(linker.readUnknownNodeAsUser());
    }

    private ObjectReferenceExprTarget createTarget() {
        ObjectReferenceExprTarget target = new ObjectReferenceExprTarget();
        target.registry = new ShelfRegistry();

        ShelfValue aurora = new ShelfValue("aurora.core", new UserValue("orbit-team"));
        ShelfLane auroraLane = new ShelfLane();
        auroraLane.values.put("node", new UserValue("iris"));
        aurora.lanes.put(2, auroraLane);
        target.registry.shelves.put("aurora.core", new ShelfSlot(aurora));

        ShelfValue terra = new ShelfValue("terra.base", new UserValue("field-team"));
        target.registry.shelves.put("terra.base", new ShelfSlot(terra));
        return target;
    }
}
