package parsing;

import com.sirweb.miro.ast.Block;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MediaIfTest {
    @Test
    public void testMaxWidth () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("@if (width <= 500px):\n    color red");
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();

        for (Block b : s.getBlocks()) {
            assertEquals("@media (max-width: 500px)", b.getHeader());
        }
    }

    @Test
    public void testHasColor () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("@if (media has color):\n    color red");
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();

        for (Block b : s.getBlocks()) {
            assertEquals("@media (color)", b.getHeader());
        }
    }

    @Test
    public void testAnd () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("@if (media has color) && (width >= 200px):\n    color red");
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();

        for (Block b : s.getBlocks()) {
            assertEquals("@media (color) and (min-width: 200px)", b.getHeader());
        }
    }

    @Test
    public void testOr () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("@if (media has color) or (width > 200px):\n    color red");
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        MiroStylesheet s = parser.parse();

        for (Block b : s.getBlocks()) {
            assertEquals("@media (color), (min-width: 201.0PX)", b.getHeader());
        }
    }


}
