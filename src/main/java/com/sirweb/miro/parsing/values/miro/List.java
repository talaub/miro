package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.MiroFuncParameterException;
import com.sirweb.miro.exceptions.MiroIndexOutOfBoundsException;
import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.parsing.values.Unit;
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
        switch (functionName) {
            case "isEmpty":
                if (parameters.size() != 0)
                    throw new MiroFuncParameterException(functionName, 0, parameters.size());
                return new Bool(values.isEmpty());
            case "length":
                if (parameters.size() != 0)
                    throw new MiroFuncParameterException(functionName, 0, parameters.size());
                return new Numeric(values.size(), Unit.NONE);
            case "get":
                if (parameters.size() != 1)
                    throw new MiroFuncParameterException(functionName, 0, parameters.size());
                MiroValue idxValue = parameters.get(0);
                if (!(idxValue instanceof Numeric))
                    throw new MiroFuncParameterException("get function parameter has to be numeric");
                if (((Numeric) idxValue).getUnit() == Unit.NONE)
                    if (((int)((Numeric) idxValue).getValue()) >= values.size())
                        throw new MiroIndexOutOfBoundsException(((int)((Numeric) idxValue).getValue()));
                    else
                        return values.get((int)((Numeric) idxValue).getValue());
                throw new MiroFuncParameterException("get function parameter has to be simple number");
            default:
                throw new MiroUnimplementedFuncException(functionName, this.getClass());

        }
    }

    public com.sirweb.miro.parsing.values.miro.List clone () {
        com.sirweb.miro.parsing.values.miro.List l = new com.sirweb.miro.parsing.values.miro.List();
        for (MiroValue v : values)
            l.addValue(v);
        return l;
    }
}
