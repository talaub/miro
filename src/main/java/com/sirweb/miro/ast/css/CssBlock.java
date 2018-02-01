package com.sirweb.miro.ast.css;


import java.util.ArrayList;
import java.util.List;

public class CssBlock implements CssElement {
    private String header;
    private List<CssStatement> statements;

    public CssBlock(String header) {
        this.header = header;
        this.statements = new ArrayList<>();

    }

    public String getHeader () { return header; }

    public void addStatement (CssStatement statement) { statements.add(statement); }

    public boolean hasStatement (String property) {
        return getStatement(property) != null;
    }

    public CssStatement getStatement (String property) {
        for (CssStatement statement : statements)
            if (statement.getProperty().equals(property))
                return statement;
        return null;
    }

    public List<CssStatement> getStatements() {
        return statements;
    }
}
