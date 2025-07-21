package io.github.hhy50.linker.test.token;

import io.github.hhy50.linker.annotations.Expr;
import io.github.hhy50.linker.token.TokenParser;
import io.github.hhy50.linker.token.Tokens;
import org.junit.Assert;
import org.junit.Test;

public class TokenParserTest {

    interface TestClass {
        @Expr("a.get('1').run($1)")
        Object exec(String arg);
    }

    @Test
    public void testParseToken1() {
        String tokenExpr = "a.b.c.d.e[12345][23456][321][1111].f['a'].get(a.b, a.c, 1, '12', $1)";
        TokenParser parser = new TokenParser();
        Tokens tokens = parser.parse(tokenExpr);
        Assert.assertEquals(tokenExpr, tokens.toString());
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
                    "At position 18 Unknown symbol [");
        }
        try {
            // ]]
            String tokenExpr = "a.b.c.d.e[12345]]['23456'][321][1111].f['a']";
            parser.parse(tokenExpr); // At position 18 Unknown symbol [
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(),
                    "At position 17 Unknown symbol ]");
        }
        try {
            // 最后一个字符为 .
            String tokenExpr = "a.b.c.d.e[12345]['23456'][321][1111].f['a'].";
            parser.parse(tokenExpr); // At position 18 Unknown symbol [
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(),
                    "At position 44 Unknown symbol .");
        }
    }
}
