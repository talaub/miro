package parsing;

import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class ParserTest {

    @Test
    public void testParserReturn () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("");
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        Object r = parser.parse();

        assertTrue(r != null);
        assertTrue(r instanceof MiroStylesheet);
    }
}
