package com.sirweb.miro.exceptions;

public class MiroException extends Exception {
    private String message;

    public MiroException (String message) {
        super(message);
        this.message = message;
    }

    public void print () {
        System.err.println("Miro could not be parsed");
        System.err.println(this.getClass().getName());
        System.err.println(message);
    }

}
