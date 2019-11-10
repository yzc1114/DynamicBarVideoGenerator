package com.yzchnb.dynamicbarvideogenerator.Web;

import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.GeneratorConfiguation;
import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Service.GeneratorService;
import com.yzchnb.dynamicbarvideogenerator.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

@RestController
public class IndexController {
    @Autowired
    GeneratorService generatorService;

    @RequestMapping(value = "/generateVideo", method = RequestMethod.POST, consumes = {MediaType.ALL_VALUE})
    @ResponseBody
    public String generateVideo(@ModelAttribute UserInputConfiguration userInputConfiguration, @RequestParam(value = "file") MultipartFile multipartFile){
        //System.out.println(userInputConfiguration);
        //UserInputConfiguration userInputConfiguration1 = new UserInputConfiguration();
//        ArrayList<String> classNames = new ArrayList<String>(){
//            {
//                add("media.datasink.E.Handler");
//                add("javax.media.datasink.E.Handler");
//                add("com.sun.media.datasink.E.Handler");
//                add("com.ibm.media.datasink.E.Handler");
//            }
//        };
//        classNames.forEach((name) -> {
//            try{
//                System.out.println(name);
//                Class.forName(name);
//            }catch (ClassNotFoundException e){
//                e.printStackTrace();
//            }
//        });


        GeneratorConfiguation generatorConfiguation = new GeneratorConfiguation(userInputConfiguration);
        if(multipartFile.isEmpty()){
            System.out.println("上传失败");
            return "上传失败";
        }
        File tempFile = new File(Utils.getDataFileDir() + userInputConfiguration.hashCode() + ".csv");
        try{
            multipartFile.transferTo(tempFile);
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        //TODO 上传csv文件
        try{
            return generatorService.generateVideo(generatorConfiguation, tempFile,Utils.getMoviesDir());
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
