package parsing;

        import com.sirweb.miro.exceptions.MiroException;
        import com.sirweb.miro.lexer.Token;
        import com.sirweb.miro.lexer.TokenType;
        import com.sirweb.miro.lexer.Tokenizer;
        import com.sirweb.miro.parsing.Parser;
        import com.sirweb.miro.parsing.values.Unit;
        import com.sirweb.miro.parsing.values.miro.Calculator;
        import com.sirweb.miro.parsing.values.miro.MiroValue;
        import com.sirweb.miro.parsing.values.miro.Numeric;
        import com.sirweb.miro.parsing.values.miro.StringValue;
        import org.junit.Test;

        import java.util.List;

        import static org.junit.Assert.assertEquals;

public class CalculatorTest {
    @Test
    public void testPostfix () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("5 + 10");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        List<Object> postfix = calculator.getPostfix();
        assertEquals(5, (int)((Numeric)postfix.get(0)).getNormalizedValue());
        assertEquals(10, (int)((Numeric)postfix.get(1)).getNormalizedValue());
        assertEquals(Calculator.Operator.PLUS, (Calculator.Operator) postfix.get(2));
    }

    @Test
    public void testValues () throws MiroException {
        Numeric pixel = new Numeric(5.0, Unit.PX);
        assertEquals(5, (int) pixel.getValue());


        Numeric em = new Numeric(16, Unit.EM);
        assertEquals(1, (int) em.getValue());

        Numeric rem = new Numeric(32, Unit.REM);
        assertEquals(2, (int) rem.getValue());

        Numeric tokEm = new Numeric(new Token("-3em", TokenType.DIMENSION_TOKEN));
        assertEquals(-3, (int) tokEm.getValue());
    }

    @Test
    public void testAddNumerics () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("5 + 10");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals(15, (int) ((Numeric) result).getNormalizedValue());
    }

    @Test
    public void testConvertEmPx () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("5px + 1em");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals(21, (int) ((Numeric) result).getValue());
        assertEquals(Unit.PX, ((Numeric) result).getUnit());
    }

    @Test
    public void testConvertPxEm () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("1em + 32px");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals(3, (int) ((Numeric) result).getValue());
        assertEquals(Unit.EM, ((Numeric) result).getUnit());
    }

    @Test
    public void testAddMultipleNumerics () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("5 + 10 + 3 + 2 + 100");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals(120, (int) ((Numeric) result).getNormalizedValue());
    }

    @Test
    public void testAddNumericString () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("5 + ' apples'");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("5 apples", ((StringValue) result).getValue());
    }

    @Test
    public void testAddNumericWithUnitString () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("5px + ''");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("5px", ((StringValue) result).getValue());
    }

    @Test
    public void testAddString () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("'Hello' + ' World'");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("Hello World", ((StringValue) result).getValue());
    }

    @Test
    public void testAddStringNumeric () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("'I am ' + 3px + ' large'");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("I am 3px large", ((StringValue) result).getValue());
    }

    @Test
    public void testMultiplyNumerics () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("3 * 7");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals(21, (int)((Numeric) result).getValue());
    }

    @Test
    public void testMultiplyMultipleNumerics () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("3 * 7 * 2 * 3");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals(126, (int)((Numeric) result).getValue());
    }

    @Test
    public void testMultFirst () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("5 + 2 * 3");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals(11, (int)((Numeric) result).getValue());
    }

    @Test
    public void testMultiplyNumericString () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("3 * \"Test\"");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("TestTestTest", ((StringValue) result).getValue());
    }

    @Test
    public void addPercent () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("50% + 100px");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("calc(50% + 100px)", result.toString());
    }

    @Test
    public void andFalse () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("50 && []");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("FALSE", result.toString());
    }

    @Test
    public void andTrue () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("50 && 'T'");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("TRUE", result.toString());
    }

    @Test
    public void orFalse () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("'' || []");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("FALSE", result.toString());
    }

    @Test
    public void orTrue () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("0 || test");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        Calculator calculator = new Calculator(parser);
        MiroValue result = calculator.eval();
        assertEquals("TRUE", result.toString());
    }

}
