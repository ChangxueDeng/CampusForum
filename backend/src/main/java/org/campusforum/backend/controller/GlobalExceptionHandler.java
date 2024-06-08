package org.campusforum.backend.controller;


import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.campusforum.backend.entity.Result;
import org.campusforum.backend.exception.ResourceNotFoundException;
import org.campusforum.backend.utils.StatusUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * @author ChangxueDeng
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public String validateExceptionHandler(ValidationException exception){
        log.warn("Resolved [{}:{}]", exception.getClass().getName(), exception.getMessage());
        return Result.failure(StatusUtils.STATUS_BAD_REQUEST, "参数错误").toJsonString();
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String validExceptionHandler(MethodArgumentNotValidException exception) {
        log.warn("Resolved [{}:{}]", exception.getClass().getName(), exception.getMessage());
        return Result.failure(StatusUtils.STATUS_BAD_REQUEST, "参数错误").toJsonString();
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public String resourceNotFoundExceptionHandler(ResourceNotFoundException exception) {
        log.warn("Resolved [{}:{}]", exception.getClass().getName(), exception.getMessage());
        return Result.failure(StatusUtils.STATUS_NOT_FOUND, "资源未找到").toJsonString();
    }
}
