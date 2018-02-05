package parsing;

import com.sirweb.miro.ast.Block;
import com.sirweb.miro.ast.Statement;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class importantTest {
    @Test
    public void shortVersion () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("div\n    color red!");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet miroStylesheet = parser.parse();

        for (Block block : miroStylesheet.getBlocks())
            for (Statement statement : block.getStatements())
                assertTrue(statement.isImportant());
    }

    @Test
    public void longVersion () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("div\n    color red !important");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        for (Block block : stylesheet.getBlocks())
            for (Statement statement : block.getStatements())
                assertTrue(statement.isImportant());
    }
}
