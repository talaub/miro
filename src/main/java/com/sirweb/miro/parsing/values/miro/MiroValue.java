package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.exceptions.MiroFuncParameterException;
import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.parsing.values.Value;

import java.util.List;

public interface MiroValue extends Value {
    Value callFunc(String functionName, List<MiroValue> parameters) throws MiroException;
}
