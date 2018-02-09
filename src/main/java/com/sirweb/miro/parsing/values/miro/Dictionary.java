package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.MiroFuncParameterException;
import com.sirweb.miro.exceptions.MiroIndexOutOfBoundsException;
import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.parsing.values.Unit;
import com.sirweb.miro.parsing.values.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dictionary implements MiroValue {
    private Map<String, MiroValue> values;

    public Dictionary () {
        values = new HashMap<>();
    }

    public boolean hasKey (String key) {
        return values.containsKey(key);
    }

    public void setValue (String key, MiroValue value) {
        values.put(key, value);
    }

    public MiroValue get (String key) {
        return hasKey(key) ? values.get(key) : null;
    }

    public Iterable<String> getKeys () { return values.keySet(); }

    @Override
    public Value callFunc(String functionName, List<MiroValue> parameters) throws MiroParserException {
        switch (functionName) {
            case "isEmpty":
                if (parameters.size() != 0)
                    throw new MiroFuncParameterException(functionName, 0, parameters.size());
                return new Bool(values.isEmpty());
            case "length":
                if (parameters.size() != 0)
                    throw new MiroFuncParameterException(functionName, 0, parameters.size());
                return new Numeric(values.keySet().size(), Unit.NONE);
            case "getAll":
                if (parameters.size() != 0)
                    throw new MiroFuncParameterException(functionName, 0, parameters.size());
                com.sirweb.miro.parsing.values.miro.List list = new com.sirweb.miro.parsing.values.miro.List();
                for (String key : values.keySet()) {
                    com.sirweb.miro.parsing.values.miro.List inner = new com.sirweb.miro.parsing.values.miro.List();
                    inner.addValue(new Ident(key));
                    inner.addValue(values.get(key));
                    list.addValue(inner);
                }
                return list;
            default:
                throw new MiroUnimplementedFuncException(functionName, this.getClass());

        }
    }

    @Override
    public boolean getBoolean() {
        return !values.keySet().isEmpty();
    }
}
