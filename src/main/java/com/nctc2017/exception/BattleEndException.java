package com.nctc2017.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class BattleEndException extends Exception{

    public BattleEndException() {
        super();
    }

    public BattleEndException(String message, Throwable cause) {
        super(message, cause);
    }

    public BattleEndException(String message) {
        super(message);
    }

    public BattleEndException(Throwable cause) {
        super(cause);
    }
    
}
