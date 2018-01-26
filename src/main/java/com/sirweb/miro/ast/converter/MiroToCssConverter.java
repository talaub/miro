package com.sirweb.miro.ast.converter;

import com.sirweb.miro.ast.Block;
import com.sirweb.miro.ast.Statement;
import com.sirweb.miro.ast.css.CssBlock;
import com.sirweb.miro.ast.css.CssStatement;
import com.sirweb.miro.ast.css.CssStylesheet;
import com.sirweb.miro.ast.miro.MiroBlock;
import com.sirweb.miro.ast.miro.MiroStylesheet;

public class MiroToCssConverter {
    MiroStylesheet stylesheet;
    public MiroToCssConverter(MiroStylesheet stylesheet) {
        this.stylesheet = stylesheet;
    }


    public CssStylesheet convert() {
        MiroStylesheet stylesheet = (MiroStylesheet) this.stylesheet;

        CssStylesheet cssStylesheet = new CssStylesheet();

        for (Block b : stylesheet.getBlocks()) {
            MiroBlock block = (MiroBlock) b;
            convertBlock(cssStylesheet, block, "");
        }

        return cssStylesheet;
    }

    private void convertBlock (CssStylesheet cssStylesheet, MiroBlock block, String preHeader) {
        String header = preHeader + block.getHeader();
        CssBlock cssBlock = cssStylesheet.hasBlock(header) ? cssStylesheet.getBlock(header)
                : new CssBlock(header);

        for (Statement statement : block.getStatements())
            cssBlock.addStatement(new CssStatement(statement.getProperty(), statement.getValue().toString()));

        cssStylesheet.addBlock(cssBlock);

        for (Block b : block.getBlocks())
            convertBlock(cssStylesheet, (MiroBlock) b, header);
    }
}
