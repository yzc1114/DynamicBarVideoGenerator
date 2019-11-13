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

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

@RestController
public class IndexController {
    @Autowired
    GeneratorService generatorService;

    @RequestMapping(value = "/generateVideo", method = RequestMethod.POST, consumes = {MediaType.ALL_VALUE})
    @ResponseBody
    public String generateVideo(@ModelAttribute UserInputConfiguration userInputConfiguration,
                                @RequestParam(value = "file") MultipartFile multipartFile,
                                HttpServletRequest request){
        //System.out.println(userInputConfiguration);
        //UserInputConfiguration userInputConfiguration1 = new UserInputConfiguration();

        GeneratorConfiguation generatorConfiguation = new GeneratorConfiguation(userInputConfiguration);
        if(multipartFile.isEmpty()){
            System.out.println("上传失败");
            return "上传失败";
        }
        File tempFile = new File(Utils.getDataFileDir() + userInputConfiguration.hashCode() + ".csv");
        request.getSession(true).setAttribute("fileId",userInputConfiguration.hashCode());
        try{
            multipartFile.transferTo(tempFile);
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        //TODO 上传csv文件
        try{
            String result=generatorService.generateVideo(generatorConfiguation, tempFile,Utils.getMoviesDir(),
                    request.getSession().getAttribute("fileId").toString());
            request.getSession().removeAttribute("fileId");
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/getRate", method = RequestMethod.GET)
    @ResponseBody
    public Double getRateOfGeneration(HttpServletRequest request){
        if(request.getSession().getAttribute("fileId")!=null){
            return generatorService.getRateOfGeneration(request.getSession().getAttribute("fileId").toString());
        }else{
            return Double.NaN;
        }
    }
}
