package com.sirweb.miro.ast.css;

import com.sirweb.miro.exceptions.MiroConvertException;

public class CssStatement {
    private String value;
    private String property;

    public CssStatement (String property, String value) {
        this.property = property;
        this.value = value;

    }

    public String getProperty () { return property; }
    public String getValue () { return value; }

    public void setValue (String newValue) throws MiroConvertException {
        if (newValue == null || newValue.isEmpty())
            throw new MiroConvertException("CSS Statement value cannot be empty");
        value = newValue;
    }
}
