package ast.convert;

import com.sirweb.miro.ast.converter.MiroToCssConverter;
import com.sirweb.miro.ast.css.CssStylesheet;
import com.sirweb.miro.ast.miro.MiroBlock;
import com.sirweb.miro.ast.miro.MiroStatement;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.export.CssExporter;
import com.sirweb.miro.parsing.values.Unit;
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
        assertTrue(cssStylesheet.getBlock("div").hasStatement("margin-right"));

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
        assertTrue(cssStylesheet.getBlock("div").hasStatement("margin-right"));
        assertTrue(cssStylesheet.getBlock("div p").hasStatement("font-weight"));
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
        assertTrue(cssStylesheet.getBlock("div").hasStatement("margin-left"));
        assertTrue(cssStylesheet.getBlock("div:hover").hasStatement("font-size"));
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
        assertTrue(cssStylesheet.getBlock("div").hasStatement("margin-left"));
        assertTrue(cssStylesheet.getBlock("div").hasStatement("font-size"));
    }
}
