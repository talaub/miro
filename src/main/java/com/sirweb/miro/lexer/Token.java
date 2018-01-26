package com.sirweb.miro.lexer;

/**
 * @author Tamino Laub
 */
public class Token {

    private String token;
    private TokenType type;

    public Token(String token, TokenType type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public TokenType getType() {
        return type;
    }

    public String toString () {
        return "<"+getType()+"  -  '"+((getType() == TokenType.NEWLINE_TOKEN)? "\\n":getToken())+"'>";

    }
}