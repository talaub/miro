package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.MiroFuncParameterException;
import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.parsing.values.Unit;
import com.sirweb.miro.parsing.values.Value;

import java.util.List;
import java.util.Map;

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

        if (tokenString.charAt(0) == '.')
            tokenString = '0' + tokenString;

        String valueString = "";
        String unitString = "";

        boolean atUnit = false;
        for (int i = 0; i < tokenString.length(); i++) {
            if (!atUnit) {
                if ("0123456789.".indexOf(tokenString.charAt(i)) != -1)
                    valueString += tokenString.charAt(i);
                else
                    atUnit = true;

            }

            if (atUnit)
                unitString += tokenString.charAt(i);
        }

        double tokenValue = Double.parseDouble(valueString);

        this.value = tokenValue * Unit.forString(unitString).getMultiplier() * (negative ? -1 : 1);
        this.unit = Unit.forString(unitString.toUpperCase().trim());

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

    @Override
    public Value callFunc(String functionName, List<MiroValue> parameters) throws MiroParserException {
        return null;
    }

    @Override
    public boolean getBoolean() {
        return getNormalizedValue() != 0.0;
    }
}
