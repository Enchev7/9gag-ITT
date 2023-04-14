package com.example.demo.model.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String msg){
        super(msg);
    }
}

