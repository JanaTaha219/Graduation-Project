package com.example.zh2t.exceptions;

import org.springframework.http.HttpStatus;

public class ObjectNotFoundException extends RuntimeException{
    String message;
    HttpStatus httpStatus;

    ObjectNotFoundException(String message, HttpStatus httpStatus){
        this.httpStatus = httpStatus;
        this.message = message;
    }

    ObjectNotFoundException(){
        this.message = "Object is not in the database";
        this.httpStatus = HttpStatus.NOT_FOUND;
    }

    public ObjectNotFoundException(String message){
        this.message = message;
        this.httpStatus = HttpStatus.NOT_FOUND;
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
