package com.sirweb.miro.ast;

import com.sirweb.miro.parsing.values.Value;

public abstract class Statement {
    private Value value;
    private String property;
    private boolean important = false;

    public Statement (String property, Value value) {
        this(property, value, false);

    }

    public Statement (String property, Value value, boolean important) {
        this.property = property;
        this.value = value;
        this.important = important;

    }

    public String getProperty () { return property; }
    public Value getValue () { return value; }

    public boolean isImportant () { return important; }
}
