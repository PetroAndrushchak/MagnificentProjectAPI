package com.petroandrushchak.controler.advice;

import com.petroandrushchak.exceptions.FutEaAccountNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class FutEaAccountNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(FutEaAccountNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String employeeNotFoundHandler(FutEaAccountNotFound ex) {
        return ex.getMessage();
    }
}