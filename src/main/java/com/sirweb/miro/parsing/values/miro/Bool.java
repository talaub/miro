package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.MiroFuncParameterException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.parsing.values.Value;

import java.util.List;

public class Bool implements MiroValue {
    private boolean value;
    public Bool (boolean val) {
        value = val;
    }
    @Override
    public Value callFunc(String functionName, List<MiroValue> parameters) throws MiroUnimplementedFuncException, MiroFuncParameterException {
        return null;
    }

    public String toString () {
        return value ? "TRUE" : "FALSE";
    }
}
