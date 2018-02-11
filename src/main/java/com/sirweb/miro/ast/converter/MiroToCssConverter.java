package com.sirweb.miro.ast.converter;

import com.sirweb.miro.ast.Block;
import com.sirweb.miro.ast.ImportRule;
import com.sirweb.miro.ast.Statement;
import com.sirweb.miro.ast.css.*;
import com.sirweb.miro.ast.miro.MiroBlock;
import com.sirweb.miro.ast.miro.MiroMediaQuery;
import com.sirweb.miro.ast.miro.MiroStylesheet;

public class MiroToCssConverter {
    private MiroStylesheet stylesheet;
    private CssMediaQuery currentMediaQuery = null;
    public MiroToCssConverter(MiroStylesheet stylesheet) {
        this.stylesheet = stylesheet;
    }


    public CssStylesheet convert() {
        MiroStylesheet stylesheet = this.stylesheet;
        new MiroAutoprefixer(stylesheet).prefix();

        CssStylesheet cssStylesheet = new CssStylesheet();

        for (Block b : stylesheet.getBlocks()) {
            MiroBlock block = (MiroBlock) b;
            if (block instanceof MiroMediaQuery)
                convertMediaQuery(cssStylesheet, (MiroMediaQuery) block, "");
            else
                convertBlock(cssStylesheet, block, "");
        }

        return cssStylesheet;
    }

    private void convertMediaQuery(CssStylesheet cssStylesheet, MiroMediaQuery mediaQuery, String preHeader) {
        String header = mediaQuery.getHeader();

        CssMediaQuery cssMediaQuery = new CssMediaQuery(header);
        this.currentMediaQuery = cssMediaQuery;

        // Add all statements that are directly inside the media query to a new block with header of the surrounding block
        // but inside the media query
        if (mediaQuery.hasStatements()) {
            MiroBlock newBlock = new MiroBlock("&");

            for (Statement statement : mediaQuery.getStatements())
                newBlock.addStatement(statement);

            mediaQuery.addBlock(newBlock);
        }

        for (Block block : mediaQuery.getBlocks())
            convertBlock(cssStylesheet, (MiroBlock) block, preHeader);

        this.currentMediaQuery = null;
        cssStylesheet.addElement(cssMediaQuery);
    }

    private void convertBlock (CssStylesheet cssStylesheet, MiroBlock block, String preHeader) {
        String header = block.getHeader();

        if (header.contains("&"))
            header = header.replace("&", preHeader);
        else
            header = preHeader + " " + header;

        header = header.trim();


        CssBlock cssBlock = cssStylesheet.hasBlock(header) ?
                (currentMediaQuery == null) ? ((CssBlock) cssStylesheet.getElement(header))
                : new CssBlock(header)
                : new CssBlock(header);

        for (ImportRule importRule : block.getImportRules())
            cssStylesheet.addImportRule(new CssImportRule(importRule.getUrlValue().toString()));

        for (Statement statement : block.getStatements())
            cssBlock.addStatement(new CssStatement(statement.getProperty(), statement.getValue().toString(), statement.isImportant()));

        if (currentMediaQuery == null) {
            if (!cssStylesheet.hasBlock(cssBlock.getHeader()))
                cssStylesheet.addElement(cssBlock);
        }
        else
            currentMediaQuery.addBlock(cssBlock);

        for (Block b : block.getBlocks())
            if (b instanceof MiroMediaQuery)
                convertMediaQuery(cssStylesheet, (MiroMediaQuery) b, header);
            else
                convertBlock(cssStylesheet, (MiroBlock) b, header);
    }
}
