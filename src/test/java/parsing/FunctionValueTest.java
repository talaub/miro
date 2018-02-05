package parsing;

import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FunctionValueTest {
    @Test
    public void simpleFunction () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = translate(20px, 30px);$test2 = rotate(5deg)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasSymbol("test"));
        assertEquals("translate(20px, 30px)", stylesheet.symbolTable().getSymbol("test").toString());
        assertTrue(stylesheet.symbolTable().hasSymbol("test2"));
        assertEquals("rotate(5deg)", stylesheet.symbolTable().getSymbol("test2").toString());
    }
}
