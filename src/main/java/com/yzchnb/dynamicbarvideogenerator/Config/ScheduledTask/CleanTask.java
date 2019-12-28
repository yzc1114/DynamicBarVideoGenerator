package com.yzchnb.dynamicbarvideogenerator.Config.ScheduledTask;

import com.yzchnb.dynamicbarvideogenerator.Logger.Logger;
import com.yzchnb.dynamicbarvideogenerator.Utils.Utils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;

@Configuration
@EnableScheduling
public class CleanTask {
    @Scheduled(fixedRate = 60 * 10 * 1000)
    public void doClean(){
        File moviesDir = new File(Utils.getMoviesDir());
        deleteOutDatedIn(moviesDir);
        File csvDir = new File(Utils.getDataFileDir());
        deleteOutDatedIn(csvDir);
    }

    private void deleteOutDatedIn(File dir){
        File[] fs = dir.listFiles();
        if(fs == null)
            return;
        for (File f : fs) {
            if(System.currentTimeMillis() - f.lastModified() > 60 * 60 * 1000){
                if(f.delete()){
                    Logger.log("delete " + f.getName() + " succeeded");
                }else{
                    Logger.log("delete " + f.getName() + "failed");
                }
            }
        }
    }
}
