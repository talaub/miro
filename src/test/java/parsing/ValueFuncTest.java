package parsing;

import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.exceptions.MiroIndentationException;
import com.sirweb.miro.exceptions.MiroIndexOutOfBoundsException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import com.sirweb.miro.parsing.values.miro.Color;
import com.sirweb.miro.parsing.values.miro.MiroValue;
import com.sirweb.miro.parsing.values.miro.Numeric;
import com.sirweb.miro.parsing.values.miro.StringValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ValueFuncTest {
    @Test
    public void colorGetRed () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = #ff0000.getRed()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("test"));
        assertEquals(255, (int) ((Numeric) s.symbolTable().getSymbol("test")).getValue());
    }

    @Test
    public void colorGetGreen () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = #ff0000.getGreen()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("test"));
        assertEquals(0, (int) ((Numeric) s.symbolTable().getSymbol("test")).getValue());
    }

    @Test
    public void colorGetBlue () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = #ff0020.getBlue()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("test"));
        assertEquals(32, (int) ((Numeric) s.symbolTable().getSymbol("test")).getValue());
    }

    @Test
    public void colorGetAlpha () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = rgba(255,255,255,255).getAlpha()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("test"));
        assertEquals(1, (int) ((Numeric) s.symbolTable().getSymbol("test")).getValue());
    }

    @Test
    public void colorSetRed () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = #ff8a20.setRed(0)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("test"));
        MiroValue v = s.symbolTable().getSymbol("test");
        assertEquals(0, (int) ((Color) s.symbolTable().getSymbol("test")).getRed());
    }

    @Test
    public void colorSetGreen () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = #ff8a20.setGreen(50%)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("test"));
        MiroValue v = s.symbolTable().getSymbol("test");
        assertEquals(127, (int) ((Color) s.symbolTable().getSymbol("test")).getGreen());
    }

    @Test
    public void colorSetBlue () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = #ff8a20.setBlue(100%)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("test"));
        MiroValue v = s.symbolTable().getSymbol("test");
        assertEquals(255, (int) ((Color) s.symbolTable().getSymbol("test")).getBlue());
    }

    @Test
    public void colorSetAlpha () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = #ff8a20.setAlpha(0%)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("test"));
        MiroValue v = s.symbolTable().getSymbol("test");
        assertEquals(0, (int) ((Color) s.symbolTable().getSymbol("test")).getAlpha());
    }

    @Test
    public void stringIsEmpty () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$empty = ''.isEmpty(); $not-empty = 'test'.isEmpty()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("empty"));
        assertTrue(s.symbolTable().hasSymbol("not-empty"));
        assertEquals("TRUE", s.symbolTable().getSymbol("empty").toString());
        assertEquals("FALSE", s.symbolTable().getSymbol("not-empty").toString());
    }

    @Test
    public void stringChar () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$c = 'ABCD'.char(2)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("c"));
        assertEquals("C", ((StringValue)s.symbolTable().getSymbol("c")).getValue());
    }

    @Test(expected = MiroIndexOutOfBoundsException.class)
    public void stringCharFail () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$c = 'ABCD'.char(8)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        parser.parse();
    }

    @Test
    public void stringLength () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$length = 'ABCD'.length()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("length"));
        assertEquals(4, (int) ((Numeric)s.symbolTable().getSymbol("length")).getValue());
    }

    @Test(expected = MiroUnimplementedFuncException.class)
    public void unknownFunction () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = #ff0000.bla()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        parser.parse();
    }

    @Test
    public void listIsEmpty () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$empty = [].isEmpty(); $not-empty = [test].isEmpty()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("empty"));
        assertTrue(s.symbolTable().hasSymbol("not-empty"));
        assertEquals("TRUE", s.symbolTable().getSymbol("empty").toString());
        assertEquals("FALSE", s.symbolTable().getSymbol("not-empty").toString());
    }

    @Test
    public void listLength () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$length = [1,2,3,4].length()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("length"));
        assertEquals(4, (int) ((Numeric)s.symbolTable().getSymbol("length")).getValue());
    }

    @Test
    public void listGet () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$c = ['test1', test2, 'test3', 12px].get(2)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("c"));
        assertEquals("test3", ((StringValue)s.symbolTable().getSymbol("c")).getValue());
    }

    @Test(expected = MiroIndexOutOfBoundsException.class)
    public void listGetFail () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$c = [1,2,3,4].get(8)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        parser.parse();
    }

    @Test
    public void dictIsEmpty () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$empty = {}.isEmpty(); $not-empty = {key: value}.isEmpty()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("empty"));
        assertTrue(s.symbolTable().hasSymbol("not-empty"));
        assertEquals("TRUE", s.symbolTable().getSymbol("empty").toString());
        assertEquals("FALSE", s.symbolTable().getSymbol("not-empty").toString());
    }

    @Test
    public void dictLength () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$length = {key1: value1, key2: value2}.length()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("length"));
        assertEquals(2, (int) ((Numeric)s.symbolTable().getSymbol("length")).getValue());
    }

    @Test
    public void dictAllValues () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$all = {key1: value1, key2: value2}.getAll();$keys = $all.length();$one = $all.get(0).length()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();
        assertTrue(s.symbolTable().hasSymbol("all"));
        assertTrue(s.symbolTable().hasSymbol("keys"));
        assertTrue(s.symbolTable().hasSymbol("one"));
        assertEquals(2, (int) ((Numeric)s.symbolTable().getSymbol("keys")).getValue());
        assertEquals(2, (int) ((Numeric)s.symbolTable().getSymbol("one")).getValue());
    }

}
