package com.mind.map.api.exceptions;

public class AlreadyExistException extends RuntimeException{
    public AlreadyExistException(String msg) {
        super(msg);
    }
}
