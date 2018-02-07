package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.parsing.values.Value;

import java.util.List;

public class Function implements MiroValue {
    private String name;
    private MultiValue parameters;

    public Function (String name, MultiValue parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName () { return name; }

    public MultiValue getParameters() { return parameters; }

    public String toString () {
        return getName() + "(" + parameters.toString() + ")";
    }

    @Override
    public Value callFunc(String functionName, List<MiroValue> parameters) throws MiroParserException {
        return null;
    }

    @Override
    public boolean getBoolean() {
        return true;
    }
}
