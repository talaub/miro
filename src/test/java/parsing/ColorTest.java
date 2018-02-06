package parsing;

import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import com.sirweb.miro.parsing.values.miro.Calculator;
import com.sirweb.miro.parsing.values.miro.Color;
import com.sirweb.miro.parsing.values.miro.MiroValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ColorTest {
    @Test
    public void testColorValues () throws MiroException {
        Color color = new Color("#73AC21");
        assertEquals("#73ac21", color.toString());

        color = new Color("#33dd00");
        assertEquals("#3d0", color.toString());

        color = new Color("#daa520");
        assertEquals("goldenrod", color.toString());

        color = new Color("#456");
        assertEquals("#456", color.toString());
    }

    @Test
    public void testParseColor () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("red");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("red", result.toString());
    }

    @Test
    public void rgb () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("rgb(0, 255, 0)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("lime", result.toString());
    }

    @Test
    public void rgba () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("rgba(0, 255, 0, 100%)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("lime", result.toString());
    }

    @Test(expected = MiroParserException.class)
    public void rgbaStringFail () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("rgba(0, 255, '5', 100%)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
    }

    @Test
    public void rgbaPercent () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("rgba(100%, 100%, 100%, 1)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("white", result.toString());
    }
}
