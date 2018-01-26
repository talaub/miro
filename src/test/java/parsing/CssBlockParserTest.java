package parsing;

import com.sirweb.miro.ast.Block;
import com.sirweb.miro.ast.Statement;
import com.sirweb.miro.ast.miro.MiroBlock;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import com.sirweb.miro.parsing.values.miro.Numeric;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CssBlockParserTest {
    @Test
    public void testParseBlock () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("div.code\n    margin-left 3px");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
        Iterable<Block> elements = stylesheet.getBlocks();
        for (Block element : elements) {
            assertEquals("div.code", ((MiroBlock) element).getHeader());
            Iterable<Statement> statements = element.getStatements();
            for (Statement statement : statements) {
                assertEquals("margin-left", statement.getProperty());
                assertEquals(3, (int)((Numeric) statement.getValue()).getNormalizedValue());
            }
        }
    }

    @Test
    public void testParseVariableStatement () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("div.code\n    $m=4px\n    margin-left $m");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
        Iterable<Block> elements = stylesheet.getBlocks();
        for (Block element : elements) {
            assertEquals("div.code", ((MiroBlock) element).getHeader());
            Iterable<Statement> statements = element.getStatements();
            for (Statement statement : statements) {
                assertEquals("margin-left", statement.getProperty());
                assertEquals(4, (int)((Numeric) statement.getValue()).getNormalizedValue());
            }
        }
    }

    @Test
    public void testparseNestedProperty () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("div.code\n    font--\n        size 3\n        weight 400\n    margin 1px");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
        Iterable<Block> elements = stylesheet.getBlocks();
        for (Block element : elements) {
            assertEquals("div.code", ((MiroBlock) element).getHeader());
            Iterable<Statement> statements = element.getStatements();
            int i = 0;
            for (Statement statement : statements)
                i++;
            assertEquals(3, i);
        }
    }
}
