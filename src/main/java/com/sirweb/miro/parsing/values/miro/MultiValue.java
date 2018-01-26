package com.sirweb.miro.parsing.values.miro;

import java.util.ArrayList;
import java.util.List;

public class MultiValue implements MiroValue {
    private List<MiroValue> values;

    public MultiValue () {
        values = new ArrayList<>();
    }

    public void addValue (MiroValue value) { values.add(value); }

    public int size () {
        return values.size();
    }

    public MiroValue get (int index) { return values.get(index); }
}
