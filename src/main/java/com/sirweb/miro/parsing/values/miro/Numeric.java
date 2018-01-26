package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.parsing.values.Unit;

public class Numeric implements MiroValue {
    private double value;
    private Unit unit;

    public Numeric (double normalizedValue, Unit unit) {
        this.unit = unit;
        this.value = normalizedValue;
    }

    public Numeric (Token token) {
        String unitString = (token.getToken() + " ").split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1].toUpperCase().trim();
        double tokenValue = Double.parseDouble(token.getToken().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);

        double val = tokenValue * Unit.forString(unitString).getMultiplier();

        this.value = val;
        this.unit = Unit.forString(unitString);

    }

    public double getNormalizedValue () { return value; }

    public double getValue () {
        return value / unit.getMultiplier();
    }

    public Unit getUnit () { return unit; }

    public String toString () {
        String s = "";
        if (((int) getValue()) == getValue())
            s += (int) getValue();
        else s += getValue();
        if (unit == Unit.PERCENT)
            s += '%';
        else if (unit != Unit.NONE)
            s += unit.toString().toLowerCase();

        return s;
    }
}
