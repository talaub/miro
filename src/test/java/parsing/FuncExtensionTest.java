package parsing;

import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import com.sirweb.miro.parsing.values.miro.Color;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FuncExtensionTest {

    @Test
    public void funcExtensionParsing () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("@Color\n    func nothing1():\n        return $this\n    func nothing2(val):\n        return $this\n    func nothing3(val = 10):\n        return $this");
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        parser.parse();

        assertTrue(Color.hasFunc("nothing1"));
        assertTrue(Color.hasFunc("nothing2"));
        assertTrue(Color.hasFunc("nothing3"));
        assertFalse(Color.hasFunc("nothing4"));
    }

    @Test
    public void funcExtensionSimpleFunc () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("@Color\n    func removeBlue():\n        $this = $this.setBlue(0)\n$co = #454545\n$newco = $co.removeBlue()");
        tokenizer.tokenize();

        Parser parser = new Parser(tokenizer);
        MiroStylesheet stylesheet = parser.parse();

        assertTrue(Color.hasFunc("removeBlue"));

        assertTrue(stylesheet.symbolTable().hasSymbol("co"));
        assertTrue(stylesheet.symbolTable().hasSymbol("newco"));

        assertEquals("#454545", stylesheet.symbolTable().getSymbol("co").toString());
        assertEquals("#454500", stylesheet.symbolTable().getSymbol("newco").toString());
    }
}
