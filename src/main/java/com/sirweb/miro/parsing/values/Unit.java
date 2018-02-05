package com.sirweb.miro.parsing.values;

public enum Unit {
    PX(1),
    EM(16),
    REM(16),
    NONE(1),
    PERCENT(1),
    VW(1),
    VH(1),
    S(1000),
    MS(1),
    DEG(1);

    private final int multiplier;

    Unit(final int newMultiplier) {
        multiplier = newMultiplier;
    }

    public int getMultiplier() { return multiplier; }

    public static Unit forString (String s) {
        switch (s) {
            case "":
                return Unit.NONE;
            case "%":
                return Unit.PERCENT;
            default:
                return Unit.valueOf(s);

        }
    }
}
