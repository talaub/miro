package com.sirweb.miro.ast;

import com.sirweb.miro.parsing.values.Value;

public abstract class ImportRule {
    private Value urlValue;

    public ImportRule (Value urlValue) {
        this.urlValue = urlValue;
    }

    public Value getUrlValue() {
        return urlValue;
    }
}
