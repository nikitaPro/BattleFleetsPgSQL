package com.nctc2017.exception;

public class MoneyLackException extends RuntimeException {

    public MoneyLackException(String message) {
        super(message);
    }
}
