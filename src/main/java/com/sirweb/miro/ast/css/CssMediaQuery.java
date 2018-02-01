package com.sirweb.miro.ast.css;

import java.util.ArrayList;
import java.util.List;

public class CssMediaQuery implements CssElement {

    private String header;
    private List<CssBlock> blocks;

    public CssMediaQuery (String header) {
        this.header = header;
        blocks = new ArrayList<>();
    }

    public void addBlock(CssBlock block) { blocks.add(block); }

    public CssBlock getBlock(String header) {
        for (CssBlock block : blocks)
            if (block.getHeader().equals(header))
                return block;
        return null;
    }

    public boolean hasBlock(String header) { return getBlock(header) != null; }

    @Override
    public String getHeader() {
        return header;
    }

    public Iterable<CssBlock> getBlocks() {
        return blocks;
    }
}
