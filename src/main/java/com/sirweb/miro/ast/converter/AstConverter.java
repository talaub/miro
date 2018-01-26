package com.sirweb.miro.ast.converter;

import com.sirweb.miro.ast.Stylesheet;

public abstract class AstConverter {
    protected Stylesheet stylesheet;
    public AstConverter (Stylesheet stylesheet) {
        this.stylesheet = stylesheet;
    }

    public abstract Stylesheet convert ();

}
