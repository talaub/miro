package com.sirweb.miro.ast;

import com.sirweb.miro.parsing.values.Value;

public abstract class Statement {
    private Value value;
    private String property;

    public Statement (String property, Value value) {
        this.property = property;
        this.value = value;

    }

    public String getProperty () { return property; }
    public Value getValue () { return value; }
}
