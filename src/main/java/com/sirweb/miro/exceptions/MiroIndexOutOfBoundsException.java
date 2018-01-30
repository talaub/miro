package com.sirweb.miro.exceptions;

public class MiroIndexOutOfBoundsException extends MiroParserException {
    public MiroIndexOutOfBoundsException(String message) {
        super(message);
    }

    public MiroIndexOutOfBoundsException(int index) {
        super("The index " + index + " does not exist");
    }
}
