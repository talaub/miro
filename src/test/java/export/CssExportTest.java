package export;

import com.sirweb.miro.ast.css.CssBlock;
import com.sirweb.miro.ast.css.CssStatement;
import com.sirweb.miro.ast.css.CssStylesheet;
import com.sirweb.miro.export.CssExporter;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CssExportTest {
    @Test
    public void testSimple () throws IOException {
        CssStylesheet stylesheet = new CssStylesheet();
        CssBlock block = new CssBlock("div");
        CssStatement statement = new CssStatement("margin-right", "3em", false);
        block.addStatement(statement);
        stylesheet.addElement(block);

        assertEquals("div {\n    margin-right: 3em;\n}\n\n", new CssExporter(stylesheet, false).export());

    }
}
