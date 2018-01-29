package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.parsing.values.Unit;

public class Numeric implements MiroValue {
    private double value;
    private Unit unit;

    public Numeric (double value, Unit unit) {
        this.unit = unit;
        this.value = value;
    }

    public Numeric (Token token) {
        boolean negative = false;

        String tokenString = token.getToken();

        while (tokenString.startsWith("+") || tokenString.startsWith("-")) {
            if (tokenString.startsWith("-"))
                negative = !negative;
            tokenString = tokenString.substring(1);
        }

        String unitString = (tokenString + " ").split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1].toUpperCase().trim();
        double tokenValue = Double.parseDouble(tokenString.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);

        this.value = tokenValue * Unit.forString(unitString).getMultiplier() * (negative ? -1 : 1);
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
