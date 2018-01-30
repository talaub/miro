package com.sirweb.miro.exceptions;

public class MiroFuncParameterException extends MiroParserException {
    public MiroFuncParameterException(String message) {
        super(message);
    }
    public MiroFuncParameterException(String funcName, int parameterCount, int passedParameterCount) {
        super("Function " + funcName + " takes " + parameterCount + " but passed " + passedParameterCount);
    }
}
