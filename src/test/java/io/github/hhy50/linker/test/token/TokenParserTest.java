package io.github.hhy50.linker.test.token;

import io.github.hhy50.linker.token.TokenParser;
import io.github.hhy50.linker.token.Tokens;
import org.junit.Assert;
import org.junit.Test;

public class TokenParserTest {

    interface TestClass {
        Object exec(String arg);
    }

    @Test
    public void testParseToken1() {
        String tokenExpr = "a.b.c.d.e[12345][23456][ 321 ][1111].f[ 'a' ].get(a.b, a.c, 1, '12', $1)";
        TokenParser parser = new TokenParser();
        Tokens tokens = parser.parse(tokenExpr);
        Assert.assertEquals(tokenExpr.replace(" ", ""), tokens.toString().replace(" ", ""));
    }

    @Test
    public void testParseToken2() {
        String tokenExpr = "a.b.c.d.e[12345]['23456'][321][1111].f['a']";
        TokenParser parser = new TokenParser();
        Tokens tokens = parser.parse(tokenExpr);
        Assert.assertEquals(tokenExpr, tokens.toString());
    }

    /**
     *
     */
    @Test
    public void testParseToken3() {
        TokenParser parser = new TokenParser();
        try {
            // [[
            String tokenExpr = "a.b.c.d.e[12345][['23456'][321][1111].f['a']";
            parser.parse(tokenExpr); // At position 18 Unknown symbol [
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(),
                    "At position 18 Identifier expected");
        }
        try {
            // ]]
            String tokenExpr = "a.b.c.d.e[12345]]['23456'][321][1111].f['a']";
            parser.parse(tokenExpr); // At position 18 Unknown symbol [
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(),
                    "At position 17 Identifier expected");
        }
        try {
            // 最后一个字符为 .
            String tokenExpr = "a.b.c.d.e[12345]['23456'][321][1111].f['a'].";
            parser.parse(tokenExpr); // At position 18 Unknown symbol [
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(),
                    "At position 45 Identifier expected end");
        }

        try {
            String tokenExpr = "a.b.c.d.e[12345]['23456'][321][1111].f['a'";
            parser.parse(tokenExpr); // At position 18 Unknown symbol [
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(),
                    "At position 43 Unclosed index");
        }

        try {
            String tokenExpr = "a.a23456'][b.c.d.e[12345]['321][1111].f.get(f.a.b.c[1]['123'].get())";
            parser.parse(tokenExpr); // At position 18 Unknown symbol [
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(),
                    "At position 9 Identifier expected");
        }
    }

    @Test
    public void testParseMethodExpr1() {
        String tokenExpr = "aasda.basdasd1.casdasd2.dasdasd2.easdasd3[12345]['23456'][321][1111].f.get('aa', 'bb', $1)";
        TokenParser parser = new TokenParser();
        Tokens tokens = parser.parse(tokenExpr);
        Assert.assertEquals(tokenExpr.replace(" ", ""), tokens.toString().replace(" ", ""));
    }

    @Test
    public void testParseMethodExpr2() {
        String tokenExpr = "a.b.c.d.e.f.get('aa', $1, $2, 'bb', 4, a['1234'][1].get(a.get(1123, '1234')), a.b, a, a.b.c.d.e.f)";
        TokenParser parser = new TokenParser();
        Tokens tokens = parser.parse(tokenExpr);
        Assert.assertEquals(tokenExpr.replace(" ", ""), tokens.toString().replace(" ", ""));
    }
}
