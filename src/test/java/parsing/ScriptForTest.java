package parsing;

import com.sirweb.miro.ast.Block;
import com.sirweb.miro.ast.Statement;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScriptForTest {

    @Test
    public void simpleListFor () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("for class in [box, circle, area]:\n    div.${$class}\n        padding 50px");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        int blocks = 0;
        for (Block block : stylesheet.getBlocks()) {
            if (blocks == 0)
                assertEquals("div.box", block.getHeader());
            else if (blocks == 1)
                assertEquals("div.circle", block.getHeader());
            else
                assertEquals("div.area", block.getHeader());

            int statements = 0;
            for (Statement statement : block.getStatements())
                statements++;

            assertEquals(1, statements);

            blocks++;
        }
        assertEquals(3, blocks);
    }

    @Test
    public void listForTwoParams () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("for index, class in [box, circle, area]:\n    div.${$class}\n        padding 50px * $index");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        int blocks = 0;
        for (Block block : stylesheet.getBlocks()) {
            if (blocks == 0)
                assertEquals("div.box", block.getHeader());
            else if (blocks == 1)
                assertEquals("div.circle", block.getHeader());
            else
                assertEquals("div.area", block.getHeader());

            int statements = 0;
            for (Statement statement : block.getStatements()) {
                assertEquals("padding", statement.getProperty());
                assertEquals((50 * blocks) + "px", statement.getValue().toString());
                statements++;
            }

            assertEquals(1, statements);

            blocks++;
        }
        assertEquals(3, blocks);
    }

    @Test
    public void iterateListScript () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$val = 0\nfor index in [0,1,2]:\n    $val = $val + 1");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
        assertTrue(stylesheet.symbolTable().hasSymbol("val"));
        assertEquals("3", stylesheet.symbolTable().getSymbol("val").toString());
    }

    @Test
    public void iterateStringSimple () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$val = ''\nfor index in 'Hello':\n    $val = $val + $index");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
        assertTrue(stylesheet.symbolTable().hasSymbol("val"));
        assertEquals("'Hello'", stylesheet.symbolTable().getSymbol("val").toString());
    }

    @Test
    public void iterateStringTwoParameters () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$val = '';$count = 0\nfor index, char in 'Hello':\n    $val = $val + $char\n    $count = $count + 1");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
        assertTrue(stylesheet.symbolTable().hasSymbol("val"));
        assertTrue(stylesheet.symbolTable().hasSymbol("count"));
        assertEquals("'Hello'", stylesheet.symbolTable().getSymbol("val").toString());
        assertEquals("5", stylesheet.symbolTable().getSymbol("count").toString());
    }

    @Test
    public void simpleDictFor () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("for elem in {key1: 'val1', key2: 'val2'}:\n    div.${$elem.get(0)}\n        content $elem.get(1)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        int blocks = 0;
        for (Block block : stylesheet.getBlocks()) {
            if (blocks == 0)
                assertEquals("div.key1", block.getHeader());
            else
                assertEquals("div.key2", block.getHeader());
            int statements = 0;
            for (Statement statement : block.getStatements())
                statements++;

            assertEquals(1, statements);

            blocks++;
        }
        assertEquals(2, blocks);
    }

    @Test
    public void dictForTwoParams () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("for class, padding in {box: 0px, circle: 50px, area: 100px}:\n    div.${$class}\n        padding $padding");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        int blocks = 0;
        for (Block block : stylesheet.getBlocks()) {
            int statements = 0;
            for (Statement statement : block.getStatements())
                statements++;

            assertEquals(1, statements);

            blocks++;
        }
        assertEquals(3, blocks);
    }

    @Test
    public void forTo () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$val = 1\nfor i to 30:\n    $val = $val + 1");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
        assertTrue(stylesheet.symbolTable().hasSymbol("val"));
        assertEquals("31", stylesheet.symbolTable().getSymbol("val").toString());
    }

    @Test(expected = MiroParserException.class)
    public void forToUnitFail () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$val = 1\nfor i to 30px:\n    $val = $val + 1");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        parser.parse();
    }

    @Test(expected = MiroParserException.class)
    public void forToParamFail () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$val = 1\nfor i, v to 30:\n    $val = $val + 1");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        parser.parse();
    }

    @Test(expected = MiroParserException.class)
    public void forToTypeFail () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$val = 1\nfor i to '30':\n    $val = $val + 1");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        parser.parse();
    }

    @Test(expected = MiroParserException.class)
    public void forUnknownFail () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$val = 1\nfor i ot 30:\n    $val = $val + 1");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        parser.parse();
    }
}
