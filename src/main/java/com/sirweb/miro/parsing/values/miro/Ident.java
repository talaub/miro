package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.MiroFuncParameterException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.parsing.values.Value;

import java.util.List;
import java.util.Map;

public class Ident implements MiroValue {
    String value;

    public Ident(Token token) {
        this.value = token.getToken();
    }

    public Ident (String ident) { value = ident; }

    public String getValue () { return value; }

    public String toString () { return getValue(); }

    @Override
    public Value callFunc(String functionName, List<MiroValue> parameters) throws MiroUnimplementedFuncException, MiroFuncParameterException {
        return null;
    }

    @Override
    public boolean getBoolean() {
        return true;
    }
}
