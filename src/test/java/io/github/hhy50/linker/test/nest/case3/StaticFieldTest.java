package io.github.hhy50.linker.test.nest.case3;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class StaticFieldTest {

    interface Case3_Linker {
        @Field.Getter("b.c.CLASS_NAME")
        String getThreeClass();

        @Field.Setter("b.c.CLASS_NAME")
        void setThreeClass(String val);
    }


    @Test
    public void test1() throws LinkerException {
        Case3_Linker linker = LinkerFactory.createStaticLinker(Case3_Linker.class, A.class);
        Assert.assertEquals(linker.getThreeClass(), C.class.getName());

        linker.setThreeClass("1234");
        Assert.assertEquals(linker.getThreeClass(), "1234");
    }
}
