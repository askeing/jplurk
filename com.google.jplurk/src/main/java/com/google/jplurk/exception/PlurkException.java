package com.google.jplurk.exception;

public class PlurkException extends Exception {

    private static final long serialVersionUID = 1L;

    public PlurkException() {

    }

    public PlurkException(String message) {
        super(message);
    }

    public PlurkException(Throwable t) {
        super(t);
    }

    public PlurkException(String message, Throwable t) {
        super(message, t);
    }
    
    public static PlurkException create(Throwable e)
    {
        if (e instanceof PlurkException) {
            return (PlurkException) e;
        }
        return new PlurkException(e);
    }
}
