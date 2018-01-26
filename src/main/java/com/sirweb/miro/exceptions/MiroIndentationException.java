package com.sirweb.miro.exceptions;

public class MiroIndentationException extends MiroException {
    public MiroIndentationException (String message) {
        super(message);
    }

    public MiroIndentationException () {
        this("Miro has to be indented with four spaces");
    }
}
