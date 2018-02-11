package parsing;

import com.sirweb.miro.ast.Block;
import com.sirweb.miro.ast.Statement;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScriptIfTest {

    @Test
    public void simpleIf () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("div\n    padding 5px\n    if TRUE:\n        color red\n    if FALSE:\n        background-color white");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        for (Block block : stylesheet.getBlocks()) {
            int i = 0;
            for (Statement statement : block.getStatements())
                i++;
            assertEquals(2, i);
        }
    }

    @Test
    public void largerId () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("div\n    padding 5px\n    if -3 + 3:\n        color red\n    if (5 + 3) || (-100 + 100):\n        background-color white");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        for (Block block : stylesheet.getBlocks()) {
            int i = 0;
            for (Statement statement : block.getStatements())
                i++;
            assertEquals(2, i);
        }
    }
}
