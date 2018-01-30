package com.sirweb.miro.exceptions;

public class MiroUnimplementedFuncException extends MiroParserException {
    public MiroUnimplementedFuncException(String funcName) {
        super("Function "+funcName + " could not be found");
    }

    public MiroUnimplementedFuncException(String funcName, Class objectClass) {
        super("Function " + funcName + " does not exist for object type " + objectClass.getSimpleName());
    }
}
