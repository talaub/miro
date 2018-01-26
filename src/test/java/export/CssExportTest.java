package export;

import com.sirweb.miro.ast.css.CssBlock;
import com.sirweb.miro.ast.css.CssStatement;
import com.sirweb.miro.ast.css.CssStylesheet;
import com.sirweb.miro.export.CssExporter;
import org.junit.Test;

import java.io.IOException;

public class CssExportTest {
    @Test
    public void testSimple () throws IOException {
        CssStylesheet stylesheet = new CssStylesheet();
        CssBlock block = new CssBlock("div");
        CssStatement statement = new CssStatement("margin-right", "3px");
        block.addStatement(statement);
        stylesheet.addBlock(block);

        System.out.println(new CssExporter(stylesheet, false).export());
    }
}
