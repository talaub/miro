package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.parsing.values.Value;

import java.util.List;

public class Url implements MiroValue {

    private String url;

    public Url (String urlValue) {
        this.url = urlValue;
    }

    public Url (Token token) { this(token.getToken().substring(5,token.getToken().length() - 2)); }

    public String toString () { return "url('"+url+"')"; }

    public String getUrl () { return url; }

    @Override
    public Value callFunc(String functionName, List<MiroValue> parameters) throws MiroParserException {
        return null;
    }

    @Override
    public boolean getBoolean() {
        return url.length() > 0;
    }
}
