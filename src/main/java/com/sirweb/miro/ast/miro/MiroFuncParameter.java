package com.sirweb.miro.ast.miro;

import com.sirweb.miro.parsing.values.miro.MiroValue;

public class MiroFuncParameter {

    private MiroValue defaultValue;
    private String name;

    public MiroFuncParameter (String name, MiroValue defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public MiroValue getDefaultValue() {
        return defaultValue;
    }

    public String getName () { return name; }
}
