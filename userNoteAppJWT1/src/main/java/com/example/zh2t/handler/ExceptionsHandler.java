package com.example.zh2t.handler;

import com.example.zh2t.BasicResponce.BasicMessageResponse;
import com.example.zh2t.exceptions.ObjectNotFoundException;
import com.example.zh2t.exceptions.ObjectNotValidException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler({ObjectNotValidException.class, InvalidDataAccessApiUsageException.class})
    public ResponseEntity<?> handleException(ObjectNotValidException exp){
        Map x= new HashMap();
        x.put("Error", exp.getErrorMessages());
        return ResponseEntity.badRequest().body(
                new BasicMessageResponse(x, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<?> handleObjectNotFountException(ObjectNotFoundException exp){
        return ResponseEntity.badRequest().body(
                new BasicMessageResponse("Error",exp.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleGeneralException(Exception exp){
//        return ResponseEntity.badRequest().body(
//                new BasicMessageResponse("Error",exp.getMessage(), HttpStatus.BAD_REQUEST.value()));
//    }


}