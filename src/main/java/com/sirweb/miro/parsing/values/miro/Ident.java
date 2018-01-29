package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.lexer.Token;

public class Ident implements MiroValue {
    String value;

    public Ident(Token token) {
        this.value = token.getToken();
    }

    public String getValue () { return value; }

    public String toString () { return getValue(); }
}
