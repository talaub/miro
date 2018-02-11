package com.sirweb.miro.ast.css;

import java.util.ArrayList;
import java.util.List;

public class CssStylesheet {
    private List<CssElement> elements;
    private List<CssImportRule> importRules;

    public CssStylesheet () {
        this.elements = new ArrayList<>();
        this.importRules = new ArrayList<>();
    }

    public List<CssElement> getElements () { return elements; }

    public boolean hasBlock (String header) {
        return getElement(header) != null;
    }

    public CssElement getElement (String header) {
        for (CssElement element : elements)
            if (element.getHeader().equals(header))
                return element;
        return null;
    }

    public void addElement(CssElement cssElement) {
        elements.add(cssElement);
    }

    public void addImportRule (CssImportRule importRule) { this.importRules.add(importRule); }

    public Iterable<CssImportRule> getImportRules () { return importRules; }
}
