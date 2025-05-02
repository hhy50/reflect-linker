package io.github.hhy50.linker.test.private_class;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Target;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.test.private_class.inner.InnerHolder;
import org.junit.Assert;
import org.junit.Test;

public class PrivateClassTest {

    interface LInnerHolder {
        @Field.Getter("inner")
        LInner getInner();
        @Field.Setter("inner")
        void setInner(LInner inner);
        LInner run(LInner lInner);
    }

    @Target.Bind("io.github.hhy50.linker.test.private_class.inner.Inner")
    interface LInner {
        @Method.Constructor
        LInner newInstance();
    }

    @Test
    public void test1() throws LinkerException, ClassNotFoundException {
        LInnerHolder innerHolder = LinkerFactory.createLinker(LInnerHolder.class, new InnerHolder());
        LInner innerLinker = LinkerFactory.createStaticLinker(LInner.class, Class.forName("io.github.hhy50.linker.test.private_class.inner.Inner"));
        Assert.assertNotNull(innerHolder.getInner());

        innerHolder.setInner(null);
        Assert.assertNull(innerHolder.getInner());

        LInner inner = innerLinker.newInstance();
        innerHolder.setInner(inner);
        Assert.assertEquals(inner, innerHolder.getInner());
        Assert.assertEquals(innerHolder.run(inner), inner);
    }

    @Autolink
    interface LInnerHolder_AutoLink {
        @Field.Getter("inner")
        LInner_Autolink getInner();

        @Field.Setter("inner")
        void setInner(LInner_Autolink inner);

        LInner_Autolink run(LInner_Autolink lInner);
    }

    interface LInner_Autolink {
        @Method.Constructor
        LInner_Autolink newInstance();
    }


//    @Test
    public void test2() throws LinkerException, ClassNotFoundException {
        LinkerFactory.setOutputPath("C:\\Users\\49168\\IdeaProjects\\reflect-linker\\target");

        LInnerHolder_AutoLink innerHolder = LinkerFactory.createLinker(LInnerHolder_AutoLink.class, new InnerHolder());
        LInner_Autolink innerLinker = LinkerFactory.createStaticLinker(LInner_Autolink.class, Class.forName("io.github.hhy50.linker.test.private_class.inner.Inner"));
        Assert.assertNotNull(innerHolder.getInner());

        innerHolder.setInner(null);
        Assert.assertNull(innerHolder.getInner());

        LInner_Autolink inner = innerLinker.newInstance();
        innerHolder.setInner(inner);
        Assert.assertEquals(inner, innerHolder.getInner());
        Assert.assertEquals(innerHolder.run(inner), inner);
    }
}
