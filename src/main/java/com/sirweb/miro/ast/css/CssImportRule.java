package com.sirweb.miro.ast.css;

public class CssImportRule {
    private String urlContent;

    public CssImportRule (String urlContent) {
        this.urlContent = urlContent;
    }

    public String getUrlContent () { return urlContent; }
}
