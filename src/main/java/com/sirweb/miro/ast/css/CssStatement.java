package com.sirweb.miro.ast.css;

import com.sirweb.miro.exceptions.MiroConvertException;

public class CssStatement {
    private String value;
    private String property;
    private boolean important = false;

    public CssStatement (String property, String value, boolean important) {
        this.property = property;
        this.value = value;
        this.important = important;

    }

    public String getProperty () { return property; }
    public String getValue () { return value; }

    public boolean isImportant () { return important; }

    public void setValue (String newValue) throws MiroConvertException {
        if (newValue == null || newValue.isEmpty())
            throw new MiroConvertException("CSS Statement value cannot be empty");
        value = newValue;
    }
}
