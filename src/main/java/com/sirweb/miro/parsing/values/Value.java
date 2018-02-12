package com.sirweb.miro.parsing.values;

import com.sirweb.miro.exceptions.MiroFuncParameterException;
import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.parsing.values.miro.MiroValue;

import java.util.List;

public interface Value {

    boolean getBoolean () throws MiroParserException;
}
