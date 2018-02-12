package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.ast.miro.MiroFunc;
import com.sirweb.miro.exceptions.MiroFuncParameterException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.parsing.values.Value;

import java.util.ArrayList;
import java.util.List;

public class Bool implements MiroValue {
    private boolean value;
    private static List<MiroFunc> funcs = new ArrayList<MiroFunc>();
    public Bool (boolean val) {
        value = val;
    }
    @Override
    public Value callFunc(String functionName, List<MiroValue> parameters) throws MiroUnimplementedFuncException, MiroFuncParameterException {
        return null;
    }

    public static void addFunc(MiroFunc miroFunc) {
        for (MiroFunc f : funcs) {
            if (f.getName().equals(miroFunc.getName())) {
                funcs.remove(f);
                break;
            }
        }
        funcs.add(miroFunc);
    }

    public String toString () {
        return value ? "TRUE" : "FALSE";
    }

    @Override
    public boolean getBoolean() {
        return value;
    }
}
