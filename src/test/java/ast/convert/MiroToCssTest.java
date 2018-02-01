package ast.convert;

import com.sirweb.miro.ast.converter.MiroToCssConverter;
import com.sirweb.miro.ast.css.CssBlock;
import com.sirweb.miro.ast.css.CssMediaQuery;
import com.sirweb.miro.ast.css.CssStylesheet;
import com.sirweb.miro.ast.miro.MiroBlock;
import com.sirweb.miro.ast.miro.MiroMediaQuery;
import com.sirweb.miro.ast.miro.MiroStatement;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.export.CssExporter;
import com.sirweb.miro.parsing.values.Unit;
import com.sirweb.miro.parsing.values.miro.Color;
import com.sirweb.miro.parsing.values.miro.Numeric;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class MiroToCssTest {

    @Test
    public void convertBlock () {
        MiroStylesheet stylesheet = new MiroStylesheet();
        MiroBlock block = new MiroBlock("div");
        MiroStatement statement = new MiroStatement("margin-right", new Numeric(3.0, Unit.PX));
        block.addStatement(statement);
        stylesheet.addBlock(block);

        CssStylesheet cssStylesheet = new MiroToCssConverter(stylesheet).convert();

        assertTrue(cssStylesheet.hasBlock("div"));
        assertTrue(((CssBlock) cssStylesheet.getElement("div")).hasStatement("margin-right"));

    }

    @Test
    public void nestBlock () {
        MiroStylesheet stylesheet = new MiroStylesheet();
        MiroBlock block = new MiroBlock("div");
        MiroStatement statement = new MiroStatement("margin-right", new Numeric(3.0, Unit.PX));
        MiroBlock innerBlock = new MiroBlock("p");
        MiroStatement innerStatement = new MiroStatement("font-weight", new Numeric(400, Unit.NONE));

        innerBlock.addStatement(innerStatement);
        block.addStatement(statement);
        block.addBlock(innerBlock);
        stylesheet.addBlock(block);

        CssStylesheet cssStylesheet = new MiroToCssConverter(stylesheet).convert();
        assertTrue(cssStylesheet.hasBlock("div"));
        assertTrue(cssStylesheet.hasBlock("div p"));
        assertTrue(((CssBlock) cssStylesheet.getElement("div")).hasStatement("margin-right"));
        assertTrue(((CssBlock) cssStylesheet.getElement("div p")).hasStatement("font-weight"));
    }

    @Test
    public void concatBlock () {
        MiroStylesheet stylesheet = new MiroStylesheet();
        MiroBlock block = new MiroBlock("div");
        MiroStatement statement = new MiroStatement("margin-left", new Numeric(3.0, Unit.PX));
        MiroBlock innerBlock = new MiroBlock("&:hover");
        MiroStatement innerStatement = new MiroStatement("font-size", new Numeric(1.4, Unit.EM));

        innerBlock.addStatement(innerStatement);
        block.addStatement(statement);
        block.addBlock(innerBlock);
        stylesheet.addBlock(block);

        CssStylesheet cssStylesheet = new MiroToCssConverter(stylesheet).convert();
        assertTrue(cssStylesheet.hasBlock("div"));
        assertTrue(cssStylesheet.hasBlock("div:hover"));
        assertTrue(((CssBlock) cssStylesheet.getElement("div")).hasStatement("margin-left"));
        assertTrue(((CssBlock) cssStylesheet.getElement("div:hover")).hasStatement("font-size"));
    }

    @Test
    public void reuseBlock () throws IOException {
        MiroStylesheet stylesheet = new MiroStylesheet();
        MiroBlock block = new MiroBlock("div");
        MiroStatement statement = new MiroStatement("margin-left", new Numeric(3.0, Unit.PX));
        MiroBlock block2 = new MiroBlock("div");
        MiroStatement innerStatement = new MiroStatement("font-size", new Numeric(1.4, Unit.EM));

        block2.addStatement(innerStatement);
        block.addStatement(statement);
        stylesheet.addBlock(block2);
        stylesheet.addBlock(block);

        CssStylesheet cssStylesheet = new MiroToCssConverter(stylesheet).convert();
        assertTrue(cssStylesheet.hasBlock("div"));
        assertTrue(((CssBlock) cssStylesheet.getElement("div")).hasStatement("margin-left"));
        assertTrue(((CssBlock) cssStylesheet.getElement("div")).hasStatement("font-size"));
    }

    @Test
    public void convertMediaQuery () throws IOException {
        MiroStylesheet stylesheet = new MiroStylesheet();
        MiroBlock block = new MiroBlock("div");
        MiroStatement statement = new MiroStatement("margin-right", new Numeric(3.0, Unit.PX));
        block.addStatement(statement);

        MiroMediaQuery mediaQuery = new MiroMediaQuery("(max-width: 400px)");

        MiroBlock innerBlock = new MiroBlock("p");
        MiroStatement innerStatement = new MiroStatement("color", new Color("#ff0000"));
        innerBlock.addStatement(innerStatement);
        mediaQuery.addBlock(innerBlock);
        block.addBlock(mediaQuery);

        stylesheet.addBlock(block);

        CssStylesheet cssStylesheet = new MiroToCssConverter(stylesheet).convert();

        assertTrue(cssStylesheet.hasBlock("div"));
        assertTrue(((CssBlock) cssStylesheet.getElement("div")).hasStatement("margin-right"));
        assertTrue(cssStylesheet.hasBlock("@media (max-width: 400px)"));
        CssMediaQuery cssMediaQuery = (CssMediaQuery) cssStylesheet.getElement("@media (max-width: 400px)");
        assertTrue(cssMediaQuery.hasBlock("div p"));
        CssBlock cssBlock = cssMediaQuery.getBlock("div p");
        assertTrue(cssBlock.hasStatement("color"));
    }

    @Test
    public void convertMediaQueryWithDirectStatements () throws IOException {
        MiroStylesheet stylesheet = new MiroStylesheet();
        MiroBlock block = new MiroBlock("div");
        MiroStatement statement = new MiroStatement("margin-right", new Numeric(3.0, Unit.PX));
        block.addStatement(statement);

        MiroMediaQuery mediaQuery = new MiroMediaQuery("(max-width: 400px)");

        MiroStatement innerStatement = new MiroStatement("color", new Color("#ff0000"));
        mediaQuery.addStatement(innerStatement);
        block.addBlock(mediaQuery);

        stylesheet.addBlock(block);

        CssStylesheet cssStylesheet = new MiroToCssConverter(stylesheet).convert();

        assertTrue(cssStylesheet.hasBlock("div"));
        assertTrue(((CssBlock) cssStylesheet.getElement("div")).hasStatement("margin-right"));
        assertTrue(cssStylesheet.hasBlock("@media (max-width: 400px)"));
        CssMediaQuery cssMediaQuery = (CssMediaQuery) cssStylesheet.getElement("@media (max-width: 400px)");
        assertTrue(cssMediaQuery.hasBlock("div"));
        CssBlock cssBlock = cssMediaQuery.getBlock("div");
        assertTrue(cssBlock.hasStatement("color"));
    }
}
