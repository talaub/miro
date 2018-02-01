package parsing;

import com.sirweb.miro.ast.Block;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MediaQueryTest {
    @Test
    public void testSimpleMediaQuery () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("@media max-width: 500px\n    color red");
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();

        for (Block b : s.getBlocks()) {
            assertEquals("@media max-width: 500px", b.getHeader());
        }
    }

    @Test
    public void testMediaQueryInterpolation () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$size = 300px\n@media max-width: ${$size}\n    color red");
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();

        for (Block b : s.getBlocks()) {
            assertEquals("@media max-width: 300px", b.getHeader());
        }
    }
}
