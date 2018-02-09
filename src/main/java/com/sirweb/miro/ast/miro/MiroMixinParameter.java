package com.sirweb.miro.ast.miro;

import com.sirweb.miro.parsing.values.miro.MiroValue;

public class MiroMixinParameter {

    private String name;
    private MiroValue defaultValue;

    public MiroMixinParameter (String name, MiroValue defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName () { return name; }

    public MiroValue getDefaultValue() {
        return defaultValue;
    }
}
