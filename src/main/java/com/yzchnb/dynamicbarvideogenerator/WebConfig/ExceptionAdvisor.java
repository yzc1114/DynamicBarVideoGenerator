package com.yzchnb.dynamicbarvideogenerator.WebConfig;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionAdvisor {
    @ExceptionHandler({ Exception.class })
    @ResponseBody
    public String handleUserException(Exception e) {
        e.printStackTrace();
        return e.getMessage();
    }
}
