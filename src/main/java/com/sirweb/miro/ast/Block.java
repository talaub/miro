package com.sirweb.miro.ast;

public abstract class Block extends Element {
    private String header;

    public Block (String header) {
        this.header = header;
    }

    public String getHeader () { return header; }
}
