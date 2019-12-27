package com.yzchnb.dynamicbarvideogenerator.Logger;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

@Component
public class Logger {

    private static File logFile = new File("log.txt");

    static {
        try{
            if(!logFile.exists())
                if(!logFile.createNewFile())
                    throw new Exception("日志文件创建失败！");
            System.setOut(new PrintStream(new FileOutputStream(logFile, true)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void log(String content){
        System.out.println(LocalDateTime.now().toString() + " " + content);
    }
}
