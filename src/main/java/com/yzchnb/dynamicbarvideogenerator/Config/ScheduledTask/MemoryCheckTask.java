package com.yzchnb.dynamicbarvideogenerator.Config.ScheduledTask;

import com.yzchnb.dynamicbarvideogenerator.DynamicBarVideoGeneratorApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class MemoryCheckTask {

    private boolean lastCheck = false;

    @Scheduled(fixedRate = 60 * 2 * 1000)
    public void doCheck(){
        if(DynamicBarVideoGeneratorApplication.isMemoryFull() && lastCheck){
            DynamicBarVideoGeneratorApplication.setMemoryFull(false);
            lastCheck = false;
        }else if(DynamicBarVideoGeneratorApplication.isMemoryFull()){
            lastCheck = true;
        }
    }
}
