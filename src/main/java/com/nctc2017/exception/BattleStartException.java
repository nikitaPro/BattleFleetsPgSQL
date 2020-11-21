package com.nctc2017.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BattleStartException extends Exception{

    public BattleStartException() {
        super();
    }

    public BattleStartException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BattleStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public BattleStartException(String message) {
        super(message);
    }

    public BattleStartException(Throwable cause) {
        super(cause);
    }
    
}
