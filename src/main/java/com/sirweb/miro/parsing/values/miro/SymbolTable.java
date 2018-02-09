package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.ast.miro.MiroMixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    private Map<String, MiroValue> values;
    private List<MiroMixin> mixins;
    public SymbolTable () {
        values = new HashMap<>();
        mixins = new ArrayList<>();

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

    public void addMixin (MiroMixin mixin) {
        for (MiroMixin mix : mixins) {
            if (mix.getName().equals(mixin.getName())) {
                mixins.remove(mix);
                break;
            }
        }
        mixins.add(mixin);
    }

    public boolean hasMixin (String name) {
        return getMixin(name) != null;
    }

    public MiroMixin getMixin (String name) {
        for (MiroMixin mix : getMixins())
            if (mix.getName().equals(name))
                return mix;
        return null;
    }

    public Iterable<MiroMixin> getMixins () { return mixins; }
}
