package io.github.hhy50.linker.test.nest.case1;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;


/**
 * <p>NestObjTest class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class NestObjTest {


    /**
     * <p>test1.</p>
     *
     * @throws LinkerException if any.
     */
    @Test
    public void test1() throws LinkerException {
        // get
        Case1_LA_Runtime obj = LinkerFactory.createLinker(Case1_LA_Runtime.class, new Object() {
            A a = new A2();
        });
        Object a = obj.get_a();
        Object b = obj.get_a_b();
        Object c = obj.get_a_b_c();
        String str = obj.get_a_c_str();
        Object c2 = obj.get_a_c();
        String str2 = obj.get_a_c_str();
        Object d = obj.get_a_d();

        Assert.assertNotNull(a);
        Assert.assertNotNull(b);
        Assert.assertNotNull(c);
        Assert.assertNotNull(c2);
        Assert.assertNotNull(str);
        Assert.assertNotNull(str2);
        Assert.assertNotNull(d);
        Assert.assertTrue( (Integer) d == 10);

        // set
        a = new A2();
        b = new B();
        c = new C("c");
        c2 = new C("c2");
        str = "new_string_1";
        str2 = "new_string_2";
        d = 20;

        obj.set_a(a);
        obj.set_a_b(b);
        obj.set_a_b_c(c);
        obj.set_a_c(c2);
        obj.set_a_b_c_str(str);
        obj.set_a_c_str(str2);
        obj.set_a_d((Integer) d);

        Assert.assertEquals(obj.get_a(), a);
        Assert.assertEquals(obj.get_a_b(), b);
        Assert.assertEquals(obj.get_a_b_c(), c);
        Assert.assertEquals(obj.get_a_b_c_str(), str);
        Assert.assertEquals(obj.get_a_c_str(), str2);
        Assert.assertEquals(obj.get_a_d(), d);
    }

    /**
     * <p>test2.</p>
     *
     * @throws LinkerException if any.
     */
    @Test
    public void test2() throws LinkerException {
        // get
        Case1_LA_Typed obj = LinkerFactory.createLinker(Case1_LA_Typed.class, new Object() {
            A a = new A2();
        });

        Object a = obj.get_a();
        Object b = obj.get_a_b();
        Object c = obj.get_a_b_c();
        String str = obj.get_a_c_str();
        Object c2 = obj.get_a_c();
        String str2 = obj.get_a_c_str();
        Object d = obj.get_a_d();

        Assert.assertNotNull(a);
        Assert.assertNotNull(b);
        Assert.assertNotNull(c);
        Assert.assertNotNull(c2);
        Assert.assertNotNull(str);
        Assert.assertNotNull(str2);
        Assert.assertNotNull(d);
        Assert.assertTrue( (Integer) d == 10);

        // set
        a = new A2();
        b = new B();
        c = new C("c");
        c2 = new C("c2");
        str = "new_string_1";
        str2 = "new_string_2";
        d = 20;

        obj.set_a(a);
        obj.setB(b);
        obj.set_a_b_c(c);
        obj.set_a_c(c2);
        obj.set_a_b_c_str(str);
        obj.get_a_c_str(str2);
        obj.set_a_d((Integer) d);

        Assert.assertEquals(obj.get_a(), a);
        Assert.assertEquals(obj.get_a_b(), b);
        Assert.assertEquals(obj.get_a_b_c(), c);
        Assert.assertEquals(obj.get_a_b_c_str(), str);
        Assert.assertEquals(obj.get_a_c_str(), str2);
        Assert.assertEquals(obj.get_a_d(), d);
    }
}
