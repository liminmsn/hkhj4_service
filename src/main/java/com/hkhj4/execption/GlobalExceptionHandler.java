package com.hkhj4.execption;

import com.hkhj4.utily.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public Result ex(Exception e){
        log.error(e.getMessage());
        return Result.error(500,e.getMessage());
    }
}
