package org.campusforum.backend.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.campusforum.backend.entity.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public String ValidateExceptionHandler(ValidationException exception){
        log.warn("Resolved [{}:{}]", exception.getClass().getName(), exception.getMessage());
        return Result.failure(HttpServletResponse.SC_BAD_REQUEST, "参数错误").toJsonString();
    }
}
