package io.github.hhy50.linker.test.arraylist;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.generate.builtin.TargetProvider;
import io.github.hhy50.linker.test.LInteger;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>LArrayListTest class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class ArrayListTest {

    /**
     * <p>test.</p>
     *
     * @throws LinkerException if any.
     */
    @Test
    public void test1() throws LinkerException {
        Object[] objects = new Object[10];
        LArrayList staticLinker = LinkerFactory.createStaticLinker(LArrayList.class, ArrayList.class);
        LArrayList list = staticLinker.newList();
        list.setElementData(objects);

        Assert.assertTrue(list instanceof TargetProvider);
        Assert.assertTrue(objects == list.getElementData());

        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add("5");
        Assert.assertEquals(objects[0], list.get(0));
        Assert.assertEquals(objects[1], list.get(1));
        Assert.assertEquals(objects[2], list.get(2));
        Assert.assertEquals(objects[3], list.get(3));
        Assert.assertEquals(objects[4], list.get(4));

        Assert.assertEquals(objects[0], list.get0());
        Assert.assertEquals(objects[1], list.get1());
        Assert.assertEquals(objects[2], list.get2());
        Assert.assertEquals(objects[3], list.get3());
        Assert.assertEquals(objects[4], list.get4());

        Assert.assertEquals(list.size(), list.size());

        LInteger modCount = list.modCount();
        modCount.setValue(10086);
        list.setModCount(modCount);

        Assert.assertEquals(modCount, 10086);
        Assert.assertEquals(list.modCount(), 10086);

        LArrayListRuntime list3 = LinkerFactory.createLinker(LArrayListRuntime.class, new ArrayList<>());
        list3.add(1);
        list3.add(2);
        list3.add(3);
        list3.add(4);
        list3.add("5");
        Assert.assertEquals(1, list3.get(0));
        Assert.assertEquals(2, list3.get(1));
        Assert.assertEquals(3, list3.get(2));
        Assert.assertEquals(4, list3.get(3));
        Assert.assertEquals("5", list3.get(4));

        LinkerFactory.createLinker(LArrayList.class, new LinkedList<>()).getTarget();
        LinkerFactory.createLinker(LArrayList.class, new Vector<>());
        LinkerFactory.createLinker(LArrayList.class, new CopyOnWriteArrayList<>());
    }


    static class ArrayList2 extends ArrayList {
        int[] ints = new int[10];
        double[] doubles = new double[10];
        int[][] ints2 = new int[10][10];
    }

    interface LArrayList2 {
        @Field.Getter("elementData[0]['a']['b']['c']['d']")
        Object get0();

        @Field.Getter("elementData[1][0][1][2][3][4][0].value")
        Integer get1();

        @Field.Getter("elementData[2]['0'][1]['2'][3]['4'][5]")
        Object get2();

        void add(Object o);

        @Field.Setter("elementData")
        void setElementData(Object elementData);

        @Field.Getter("ints[0]")
        int getInt0();
    }

    @Test
    public void test2() throws LinkerException {
        Object[] objects = new Object[10];
        LArrayList2 list = LinkerFactory.createLinker(LArrayList2.class, new ArrayList2());
        list.setElementData(objects);

        list.add(new HashMap() {{
            put("a", new HashMap() {{
                put("b", new HashMap() {{
                    put("c", new HashMap() {{
                        put("d", 10);
                    }});
                }});
            }});
        }});
        list.add(new Object[]{
                new Object[]{  // 0
                        new Object[]{},
                        new Object[]{ // 1
                                new Object[]{},
                                new Object[]{},
                                new Object[]{ // 2
                                        new Object[]{},
                                        new Object[]{},
                                        new Object[]{},
                                        new Object[]{  // 3
                                                new Object[]{},
                                                new Object[]{},
                                                new Object[]{},
                                                new Object[]{},
                                                new Object[]{ // 4
                                                        Integer.valueOf(20)
                                                },
                                        },
                                }
                        },
                },
                new Object[]{},
        });
        list.add(new HashMap() {{
            put("0", new Object[]{
                    new Object[]{},
                    new HashMap() {
                        {
                            put("2", new Object[]{
                                    new Object[]{},
                                    new Object[]{},
                                    new Object[]{},
                                    new HashMap() {{
                                        put("4", new Object[]{
                                                new Object[]{},
                                                new Object[]{},
                                                new Object[]{},
                                                new Object[]{},
                                                new Object[]{},
                                                Integer.valueOf(30)});

                                    }}
                            });
                        }
                    },
            });
        }});
        list.add(4);
        list.add("5");
        Assert.assertEquals(list.get0(), 10);
        Assert.assertEquals(list.get1(), Integer.valueOf(20));
        Assert.assertEquals(list.get2(), Integer.valueOf(30));
        Assert.assertEquals(list.getInt0(), 0);
//        Assert.assertEquals(objects[1], list.get(1));
//        Assert.assertEquals(objects[2], list.get(2));
//        Assert.assertEquals(objects[3], list.get(3));
//        Assert.assertEquals(objects[4], list.get(4));
    }


    public static void check() {

    }
}
