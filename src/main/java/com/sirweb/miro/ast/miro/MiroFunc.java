package com.sirweb.miro.ast.miro;

import com.sirweb.miro.lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class MiroFunc {

    private String name;
    private List<Token> content;

    private List<MiroFuncParameter> parameters;

    public MiroFunc(String name) {
        this.name = name;
        this.parameters = new ArrayList<>();
        content = new ArrayList<>();
    }

    public String getName () { return name; }

    public void addParameter (MiroFuncParameter parameter) { parameters.add(parameter); }

    public void addContent (Token token) { content.add(token); }

    public MiroFuncParameter getParameter (int i) { return parameters.get(i); }

    public List<Token> getContent () { return content; }

    public Iterable<MiroFuncParameter> getParameters () { return parameters; }

    public int getParameterCount() {
        return parameters.size();
    }
}
