package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.MiroFuncParameterException;
import com.sirweb.miro.exceptions.MiroIndexOutOfBoundsException;
import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.parsing.values.Unit;
import com.sirweb.miro.parsing.values.Value;

import java.util.List;

public class StringValue implements MiroValue {
    private String value;

    public StringValue (String value) {
        this.value = value;
    }

    public StringValue (Token token) {
        this(token.getToken().substring(1, token.getToken().length() - 1));
    }

    public String getValue () { return value; }

    public String toString () { return "'"+getValue()+"'"; }

    @Override
    public Value callFunc(String functionName, List<MiroValue> parameters) throws MiroParserException {
        switch (functionName) {
            case "isEmpty":
                if (parameters.size() != 0)
                    throw new MiroFuncParameterException(functionName, 0, parameters.size());
                return new Bool(value.isEmpty());
            case "length":
                if (parameters.size() != 0)
                    throw new MiroFuncParameterException(functionName, 0, parameters.size());
                return new Numeric(value.length(), Unit.NONE);
            case "char":
                if (parameters.size() != 1)
                    throw new MiroFuncParameterException(functionName, 0, parameters.size());
                MiroValue idxValue = parameters.get(0);
                if (!(idxValue instanceof Numeric))
                    throw new MiroFuncParameterException("char function parameter has to be numeric");
                if (((Numeric) idxValue).getUnit() == Unit.NONE)
                    if (((int)((Numeric) idxValue).getValue()) >= value.length())
                        throw new MiroIndexOutOfBoundsException(((int)((Numeric) idxValue).getValue()));
                    else
                        return new StringValue(value.charAt((int)((Numeric) idxValue).getValue()) + "");
                throw new MiroFuncParameterException("char function parameter has to be simple number");
            default:
                throw new MiroUnimplementedFuncException(functionName, this.getClass());

        }
    }
}
