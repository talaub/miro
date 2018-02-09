package parsing;

import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import com.sirweb.miro.parsing.values.miro.Dictionary;
import com.sirweb.miro.parsing.values.miro.MiroValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DictionaryTest {
    @Test
    public void emptyDictionary () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = {}");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasSymbol("test"));

        int i = 0;
        for (String key : ((Dictionary) stylesheet.symbolTable().getSymbol("test")).getKeys())
            i++;

        assertEquals(0, i);
    }

    @Test
    public void oneValueDictionary () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = {col : #4bb3bb}");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasSymbol("test"));

        int i = 0;
        for (String key : ((Dictionary) stylesheet.symbolTable().getSymbol("test")).getKeys())
            i++;

        assertEquals(1, i);

        Dictionary dict = (Dictionary) stylesheet.symbolTable().getSymbol("test");

        assertTrue(dict.hasKey("col"));
        assertFalse(dict.hasKey("unknown"));

        assertEquals("#4bb3bb", dict.get("col").toString());
    }

    @Test
    public void multipleValueDictionary () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = {color : #4bb3bb, other-color: #73ac21, number: 42}");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasSymbol("test"));

        int i = 0;
        for (String key : ((Dictionary) stylesheet.symbolTable().getSymbol("test")).getKeys())
            i++;

        assertEquals(3, i);

        Dictionary dict = (Dictionary) stylesheet.symbolTable().getSymbol("test");

        assertTrue(dict.hasKey("color"));
        assertTrue(dict.hasKey("other-color"));
        assertTrue(dict.hasKey("number"));

        assertEquals("#4bb3bb", dict.get("color").toString());
        assertEquals("#73ac21", dict.get("other-color").toString());
        assertEquals("42", dict.get("number").toString());
    }

    @Test
    public void multilineValueDictionary () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test = {\n        color:\n            #4bb3bb,\n        other-color: #73ac21,\n        number: 42\n    }");
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(stylesheet.symbolTable().hasSymbol("test"));

        int i = 0;
        for (String key : ((Dictionary) stylesheet.symbolTable().getSymbol("test")).getKeys())
            i++;

        assertEquals(3, i);

        Dictionary dict = (Dictionary) stylesheet.symbolTable().getSymbol("test");

        assertTrue(dict.hasKey("color"));
        assertTrue(dict.hasKey("other-color"));
        assertTrue(dict.hasKey("number"));

        assertEquals("#4bb3bb", dict.get("color").toString());
        assertEquals("#73ac21", dict.get("other-color").toString());
        assertEquals("42", dict.get("number").toString());
    }
}
