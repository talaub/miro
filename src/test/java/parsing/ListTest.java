package parsing;

import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import com.sirweb.miro.parsing.values.miro.List;
import com.sirweb.miro.parsing.values.miro.MiroValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListTest {
    @Test
    public void simpleList () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = [3px]");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasSymbol("test"));
        for (MiroValue value : ((List) stylesheet.symbolTable().getSymbol("test")).getValues())
            assertEquals("3px", value.toString());

    }

    @Test
    public void emptyList () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = []");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasSymbol("test"));

        int i = 0;
        for (MiroValue value : ((List) stylesheet.symbolTable().getSymbol("test")).getValues())
            i++;

        assertEquals(0, i);

    }

    @Test
    public void multilineList () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = [3px,\n5px]");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasSymbol("test"));

        int i = 0;
        for (MiroValue value : ((List) stylesheet.symbolTable().getSymbol("test")).getValues())
            i++;

        assertEquals(2, i);

    }
}
