package com.yzchnb.dynamicbarvideogenerator.Config.WebConfig;

import com.yzchnb.dynamicbarvideogenerator.DynamicBarVideoGeneratorApplication;
import com.yzchnb.dynamicbarvideogenerator.Logger.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ExceptionAdvisor {
    @ExceptionHandler({ Exception.class, Error.class })
    @ResponseBody
    public String handleUserException(Throwable e) {
        if(e instanceof OutOfMemoryError){
            DynamicBarVideoGeneratorApplication.setMemoryFull(true);
        }
        e.printStackTrace();
        Logger.log(e.getMessage());
        return e.getMessage();
    }
}
