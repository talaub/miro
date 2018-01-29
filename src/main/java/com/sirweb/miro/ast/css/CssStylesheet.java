package com.sirweb.miro.ast.css;

import java.util.ArrayList;
import java.util.List;

public class CssStylesheet {
    private List<CssBlock> blocks;

    public CssStylesheet () {
        this.blocks = new ArrayList<>();
    }

    public List<CssBlock> getBlocks () { return blocks; }

    public boolean hasBlock (String header) {
        return getBlock(header) != null;
    }

    public CssBlock getBlock (String header) {
        for (CssBlock block : blocks)
            if (block.getHeader().equals(header))
                return block;
        return null;
    }

    public void addBlock(CssBlock cssBlock) {
        blocks.add(cssBlock);
    }
}
