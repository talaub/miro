package parsing;

import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import com.sirweb.miro.parsing.values.miro.Numeric;
import com.sirweb.miro.parsing.values.miro.StringValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScriptParserTest {
    @Test
    public void testAssign () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = 5em\n$string = \"Test\"");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
        assertEquals((int)(5.0 * 16.0), (int)((Numeric) stylesheet.symbolTable().getSymbol("test")).getNormalizedValue());
        assertEquals("Test", ((StringValue) stylesheet.symbolTable().getSymbol("string")).getValue());
    }

    @Test
    public void testAssignVariable () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = 5%; $test2 = $test");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
        assertEquals(5, (int)((Numeric) stylesheet.symbolTable().getSymbol("test2")).getNormalizedValue());
    }

    @Test(expected = MiroParserException.class)
    public void testUnknownVariable () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = $unknownVariable");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        parser.parse();
    }

    @Test
    public void testDynamicTyping () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = 5; $test = \"Test\"");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
        assertEquals("Test", ((StringValue) stylesheet.symbolTable().getSymbol("test")).getValue());

    }

    @Test(expected = MiroParserException.class)
    public void testUnexpectedToken () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = =");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        parser.parse();
    }
}
