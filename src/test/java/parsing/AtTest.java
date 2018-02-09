package parsing;

import com.sirweb.miro.ast.Block;
import com.sirweb.miro.ast.Statement;
import com.sirweb.miro.ast.miro.MiroBlock;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AtTest {

    @Test
    public void viewport () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("@viewport\n    min-width 300px");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        for (Block block : stylesheet.getBlocks()) {
            assertEquals("@viewport", block.getHeader());
            int i = 0;
            for (Statement s : block.getStatements())
                i++;
            assertEquals(1, i);
        }
    }

    @Test
    public void keyframes () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("@keyframes spin\n    0%\n        transform rotate(0deg)\n    100%\n        transform rotate(360deg)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
        for (Block block : stylesheet.getBlocks()) {
            assertEquals("@keyframes spin", block.getHeader());
            int i = 0;
            for (Statement s : block.getStatements())
                i++;
            assertEquals(0, i);

            int blocks = 0;
            for (Block b : block.getBlocks())
                blocks++;
            assertEquals(2, blocks);
        }
    }
}
