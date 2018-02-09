package parsing;

import com.sirweb.miro.ast.Block;
import com.sirweb.miro.ast.Statement;
import com.sirweb.miro.ast.miro.MiroMixin;
import com.sirweb.miro.ast.miro.MiroMixinParameter;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.exceptions.MiroMixinException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import org.junit.Test;

import static org.junit.Assert.*;

public class MixinTest {

    @Test
    public void declareMixin () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix()\n    color red");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasMixin("mix"));

        int params = 0;
        for (MiroMixinParameter param : stylesheet.symbolTable().getMixin("mix").getParameters())
            params++;

        assertEquals(0, params);
    }

    @Test
    public void declareMixinWithParameter () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix(x)\n    color red");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasMixin("mix"));

        int params = 0;
        for (MiroMixinParameter param : stylesheet.symbolTable().getMixin("mix").getParameters())
            params++;

        assertEquals(1, params);

        assertTrue(stylesheet.symbolTable().getMixin("mix").hasParameter("x"));
        assertNull(stylesheet.symbolTable().getMixin("mix").getParameter("x").getDefaultValue());
    }

    @Test
    public void declareMixinWithParameterDefaultValue () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix(x = 20px)\n    color red");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasMixin("mix"));

        int params = 0;
        for (MiroMixinParameter param : stylesheet.symbolTable().getMixin("mix").getParameters())
            params++;

        assertEquals(1, params);

        assertTrue(stylesheet.symbolTable().getMixin("mix").hasParameter("x"));
        assertNotNull(stylesheet.symbolTable().getMixin("mix").getParameter("x").getDefaultValue());
    }

    @Test
    public void declareMixinWithMultipleParameters () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix(x, y)\n    color red");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasMixin("mix"));

        int params = 0;
        for (MiroMixinParameter param : stylesheet.symbolTable().getMixin("mix").getParameters())
            params++;

        assertEquals(2, params);

        assertTrue(stylesheet.symbolTable().getMixin("mix").hasParameter("x"));
        assertNull(stylesheet.symbolTable().getMixin("mix").getParameter("x").getDefaultValue());

        assertTrue(stylesheet.symbolTable().getMixin("mix").hasParameter("y"));
        assertNull(stylesheet.symbolTable().getMixin("mix").getParameter("y").getDefaultValue());
    }

    @Test
    public void declareMixinWithMultipleParametersAllHavingDefaultValues () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix(x = 10px, y = 20px)\n    color red");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasMixin("mix"));

        int params = 0;
        for (MiroMixinParameter param : stylesheet.symbolTable().getMixin("mix").getParameters())
            params++;

        assertEquals(2, params);

        assertTrue(stylesheet.symbolTable().getMixin("mix").hasParameter("x"));
        assertNotNull(stylesheet.symbolTable().getMixin("mix").getParameter("x").getDefaultValue());

        assertTrue(stylesheet.symbolTable().getMixin("mix").hasParameter("y"));
        assertNotNull(stylesheet.symbolTable().getMixin("mix").getParameter("y").getDefaultValue());
    }

    @Test
    public void declareMixinWithMultipleParametersMixedDefaultValues () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix(x, y = 20px)\n    color red");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasMixin("mix"));

        int params = 0;
        for (MiroMixinParameter param : stylesheet.symbolTable().getMixin("mix").getParameters())
            params++;

        assertEquals(2, params);

        assertTrue(stylesheet.symbolTable().getMixin("mix").hasParameter("x"));
        assertNull(stylesheet.symbolTable().getMixin("mix").getParameter("x").getDefaultValue());

        assertTrue(stylesheet.symbolTable().getMixin("mix").hasParameter("y"));
        assertNotNull(stylesheet.symbolTable().getMixin("mix").getParameter("y").getDefaultValue());
    }

    @Test
    public void callMixinWithoutParameters () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix()\n    color red\ndiv\n    $mix()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        for (Block block : stylesheet.getBlocks()) {
            assertEquals("div", block.getHeader());
            assertTrue(block.hasStatements());

            for (Statement statement : block.getStatements()) {
                assertEquals("color", statement.getProperty());
                assertEquals("red", statement.getValue().toString());
            }
        }

    }

    @Test
    public void callMixinWithOneParameter () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix(x)\n    padding $x\ndiv\n    $mix(50px)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        for (Block block : stylesheet.getBlocks()) {
            assertEquals("div", block.getHeader());
            assertTrue(block.hasStatements());

            for (Statement statement : block.getStatements()) {
                assertEquals("padding", statement.getProperty());
                assertEquals("50px", statement.getValue().toString());
            }
        }

    }

    @Test
    public void callMixinWithMultipleParameters () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix(x, y)\n    padding $x $y\ndiv\n    $mix(50px, 20px)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        for (Block block : stylesheet.getBlocks()) {
            assertEquals("div", block.getHeader());
            assertTrue(block.hasStatements());

            for (Statement statement : block.getStatements()) {
                assertEquals("padding", statement.getProperty());
                assertEquals("50px 20px", statement.getValue().toString());
            }
        }

    }

    @Test
    public void callMixinWithOuterValues () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = 5px\n$mix()\n    padding $test\ndiv\n    $mix()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        for (Block block : stylesheet.getBlocks()) {
            assertEquals("div", block.getHeader());
            assertTrue(block.hasStatements());

            for (Statement statement : block.getStatements()) {
                assertEquals("padding", statement.getProperty());
                assertEquals("5px", statement.getValue().toString());
            }
        }

    }

    @Test(expected = MiroMixinException.class)
    public void declareMixinWrongWay () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix(color = #4bb3bb, x)\n    color $color\ndiv\n    $mix()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
    }


    @Test
    public void callMixinWithDefaultValue () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix(color = #4bb3bb)\n    color $color\ndiv\n    $mix()");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        for (Block block : stylesheet.getBlocks()) {
            assertEquals("div", block.getHeader());
            assertTrue(block.hasStatements());

            for (Statement statement : block.getStatements()) {
                assertEquals("color", statement.getProperty());
                assertEquals("#4bb3bb", statement.getValue().toString());
            }
        }
    }

    @Test
    public void callMixinOverrideDefaultValue () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix(color = #4bb3bb)\n    color $color\ndiv\n    $mix(#73ac21)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        for (Block block : stylesheet.getBlocks()) {
            assertEquals("div", block.getHeader());
            assertTrue(block.hasStatements());

            for (Statement statement : block.getStatements()) {
                assertEquals("color", statement.getProperty());
                assertEquals("#73ac21", statement.getValue().toString());
            }
        }
    }

    @Test
    public void callMixinOverrideSome () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix(left, right, top = 5px, bottom = 5px)\n    padding $top $right $bottom $left\ndiv\n    $mix(15px, 16px, 10px)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        for (Block block : stylesheet.getBlocks()) {
            assertEquals("div", block.getHeader());
            assertTrue(block.hasStatements());

            for (Statement statement : block.getStatements()) {
                assertEquals("padding", statement.getProperty());
                assertEquals("10px 16px 5px 15px", statement.getValue().toString());
            }
        }
    }

    @Test(expected = MiroMixinException.class)
    public void callMixinDontSetAll () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$mix(left, right, top = 5px, bottom = 5px)\n    padding $top $right $bottom $left\ndiv\n    $mix(15px)");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();
    }
}
