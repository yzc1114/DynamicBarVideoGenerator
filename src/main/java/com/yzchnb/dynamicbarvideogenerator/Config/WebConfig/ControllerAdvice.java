package com.yzchnb.dynamicbarvideogenerator.Config.WebConfig;

import com.yzchnb.dynamicbarvideogenerator.DynamicBarVideoGeneratorApplication;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAdvice {
    @Pointcut("execution(public * com.yzchnb.dynamicbarvideogenerator.Controller..*(..))")
    private void addAdvice() {}

    @Around("addAdvice()")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable{
        if(DynamicBarVideoGeneratorApplication.isMemoryFull()){
            throw new Exception("Memory Full, Please Wait");
        }else{
            return pjp.proceed();
        }
    }
}
