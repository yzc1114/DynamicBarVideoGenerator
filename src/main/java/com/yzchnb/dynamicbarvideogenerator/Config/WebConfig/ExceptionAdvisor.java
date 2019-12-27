package com.yzchnb.dynamicbarvideogenerator.Config.WebConfig;

import com.yzchnb.dynamicbarvideogenerator.Logger.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ExceptionAdvisor {
    @ExceptionHandler({ Exception.class })
    @ResponseBody
    public String handleUserException(Exception e) {
        e.printStackTrace();
        Logger.log(e.getMessage());
        return e.getMessage();
    }
}
