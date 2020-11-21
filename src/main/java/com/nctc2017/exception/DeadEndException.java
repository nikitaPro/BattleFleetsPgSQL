package com.nctc2017.exception;

public class DeadEndException extends Exception {
    
    public DeadEndException() {
        super();
    }

    public DeadEndException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeadEndException(String message) {
        super(message);
    }

    public DeadEndException(Throwable cause) {
        super(cause);
    }
}
