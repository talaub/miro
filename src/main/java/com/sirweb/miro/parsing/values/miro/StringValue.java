package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.lexer.Token;

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
}
