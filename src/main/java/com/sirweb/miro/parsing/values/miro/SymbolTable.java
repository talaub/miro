package com.sirweb.miro.parsing.values.miro;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, MiroValue> values;
    public SymbolTable () {
        values = new HashMap<>();

    }

    public MiroValue getSymbol(String symbolName) {
        return values.get(symbolName);
    }

    public boolean hasSymbol (String symbolName) {
        return values.containsKey(symbolName);
    }

    public void setSymbol(String symbolName, MiroValue value) {
        values.put(symbolName, value);
    }

    public Iterable<String> getSymbols () { return values.keySet(); }
}
