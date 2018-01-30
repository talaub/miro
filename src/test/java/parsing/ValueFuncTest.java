package parsing;

import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import com.sirweb.miro.parsing.values.miro.Color;
import com.sirweb.miro.parsing.values.miro.MiroValue;
import com.sirweb.miro.parsing.values.miro.Numeric;
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

    @Test(expected = MiroUnimplementedFuncException.class)
    public void unknownFunction () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = #ff0000.bla()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        parser.parse();
    }
}
