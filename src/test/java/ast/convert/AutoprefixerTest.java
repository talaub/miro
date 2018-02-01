package ast.convert;

import com.sirweb.miro.ast.converter.MiroToCssConverter;
import com.sirweb.miro.ast.css.CssBlock;
import com.sirweb.miro.ast.css.CssStylesheet;
import com.sirweb.miro.ast.miro.MiroBlock;
import com.sirweb.miro.ast.miro.MiroStatement;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.lexer.TokenType;
import com.sirweb.miro.parsing.values.miro.Ident;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AutoprefixerTest {
    @Test
    public void testAutoprefix () {
        MiroStylesheet stylesheet = new MiroStylesheet();
        MiroBlock block = new MiroBlock("div");
        MiroStatement statement = new MiroStatement("transition", new Ident(new Token("test", TokenType.IDENT_TOKEN)));
        block.addStatement(statement);
        stylesheet.addBlock(block);

        MiroToCssConverter converter = new MiroToCssConverter(stylesheet);
        CssStylesheet cssStylesheet = converter.convert();

        assertTrue(cssStylesheet.hasBlock("div"));
        assertTrue(((CssBlock)cssStylesheet.getElement("div")).hasStatement("transition"));
        assertTrue(((CssBlock)cssStylesheet.getElement("div")).hasStatement("-webkit-transition"));
        assertTrue(((CssBlock)cssStylesheet.getElement("div")).hasStatement("-moz-transition"));
    }
}
