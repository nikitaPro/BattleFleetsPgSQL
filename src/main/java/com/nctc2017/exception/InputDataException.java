package com.nctc2017.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class InputDataException extends Exception{
    public InputDataException(String message){
        super(message);
    }
}
