package com.sirweb.miro.ast.css;

import java.util.ArrayList;
import java.util.List;

public class CssStylesheet {
    private List<CssElement> elements;

    public CssStylesheet () {
        this.elements = new ArrayList<>();
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
}
