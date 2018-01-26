package ast.convert;

import com.sirweb.miro.ast.converter.MiroToCssConverter;
import com.sirweb.miro.ast.css.CssStylesheet;
import com.sirweb.miro.ast.miro.MiroBlock;
import com.sirweb.miro.ast.miro.MiroStatement;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.parsing.values.Unit;
import com.sirweb.miro.parsing.values.miro.Numeric;
import org.junit.Test;

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
}
