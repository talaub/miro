package com.sirweb.miro.ast.miro;

import com.sirweb.miro.ast.css.CssElement;

public class MiroMediaQuery extends MiroBlock implements CssElement{
    public MiroMediaQuery(String header) {
        super("@media " + header.trim());
    }
}
