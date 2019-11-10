package com.yzchnb.dynamicbarvideogenerator.Web;

import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.GeneratorConfiguation;
import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Service.GeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;

@RestController
public class IndexController {
    @Autowired
    GeneratorService generatorService;
    @Value("${uploadDataFilePath}")
    String dataFilePath;

    @RequestMapping(value = "/generateVideo", method = RequestMethod.POST, consumes = {MediaType.ALL_VALUE})
    @ResponseBody
    public String generateVideo(@ModelAttribute UserInputConfiguration userInputConfiguration, @RequestParam(value = "file") MultipartFile multipartFile){
        //System.out.println(userInputConfiguration);
        //UserInputConfiguration userInputConfiguration1 = new UserInputConfiguration();
        GeneratorConfiguation generatorConfiguation = new GeneratorConfiguation(userInputConfiguration);
        if(multipartFile.isEmpty()){
            System.out.println("上传失败");
            return "上传失败";
        }
        File tempFile = new File(dataFilePath + userInputConfiguration.hashCode() + ".csv");
        try{
            multipartFile.transferTo(tempFile);
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        //TODO 上传csv文件
        try{
            return generatorService.generateVideo(generatorConfiguation, tempFile);
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
