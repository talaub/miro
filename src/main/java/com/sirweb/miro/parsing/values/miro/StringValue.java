package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.MiroFuncParameterException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.parsing.values.Value;

import java.util.List;
import java.util.Map;

public class StringValue implements MiroValue {
    private String value;

    public StringValue (String value) {
        this.value = value;
    }

    public StringValue (Token token) {
        this(token.getToken().substring(1, token.getToken().length() - 1));
    }

    public String getValue () { return value; }

    public String toString () { return "'"+getValue()+"'"; }

    @Override
    public Value callFunc(String functionName, List<MiroValue> parameters) throws MiroUnimplementedFuncException, MiroFuncParameterException {
        return null;
    }
}
