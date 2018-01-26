package com.sirweb.miro.ast.miro;

import com.sirweb.miro.ast.Statement;
import com.sirweb.miro.parsing.values.miro.MiroValue;

public class MiroStatement extends Statement {
    public MiroStatement(String property, MiroValue value) {
        super(property, value);
    }
}
