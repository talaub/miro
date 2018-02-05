package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.parsing.values.Value;

import java.util.ArrayList;

public class List implements MiroValue {
    java.util.List<MiroValue> values;

    public List () {
        values = new ArrayList<>();
    }

    public void addValue (MiroValue value) { values.add(value); }

    public String toString () {
        String val = "";

        for (MiroValue value : values)
            val += " " + value;

        return val.substring(1);
    }

    public Iterable<MiroValue> getValues () { return values; }

    @Override
    public Value callFunc(String functionName, java.util.List<MiroValue> parameters) throws MiroParserException {
        return null;
    }
}
