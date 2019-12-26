package com.yzchnb.dynamicbarvideogenerator.Web;

import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.GeneratorConfiguration;
import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Service.IGeneratorService;
import com.yzchnb.dynamicbarvideogenerator.Service.impls.GeneratorService;
import com.yzchnb.dynamicbarvideogenerator.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class IndexController {
    @Autowired
    private IGeneratorService generatorService;

    @RequestMapping(value = "/checkParams", method = RequestMethod.POST, consumes = {MediaType.ALL_VALUE})
    @ResponseBody
    public String checkParams(@ModelAttribute UserInputConfiguration userInputConfiguration,
                                @RequestParam(value = "file") MultipartFile multipartFile,
                                HttpServletRequest request) {
        GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.from(userInputConfiguration);
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
        String result = null;
        //上传csv文件
        try{
            generatorService.checkParams(generatorConfiguration, tempFile);
            result = "success";
        }catch (Exception e){
            e.printStackTrace();
            result = e.getMessage();
        }finally {
            return result;
        }
    }

    @RequestMapping(value = "/generateVideo", method = RequestMethod.POST, consumes = {MediaType.ALL_VALUE})
    @ResponseBody
    public String generateVideo(@ModelAttribute UserInputConfiguration userInputConfiguration,
                                @RequestParam(value = "file") MultipartFile multipartFile,
                                HttpServletRequest request){
        //System.out.println(userInputConfiguration);
        //UserInputConfiguration userInputConfiguration1 = new UserInputConfiguration();
        if(request.getSession(false) != null){
            if(request.getSession(false).getAttribute("fileId") != null){
                return null;
            }
        }
        request.getSession(true).setAttribute("fileId", userInputConfiguration.hashCode());

        GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.from(userInputConfiguration);
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
        String result = null;
        //上传csv文件
        try{
             result = generatorService.generateVideo(generatorConfiguration, tempFile, Utils.getMoviesDir(),
                    request.getSession().getAttribute("fileId").toString());

        }catch (Exception e){
            e.printStackTrace();
            result =e.getMessage();
        }finally {
            request.getSession(true).removeAttribute("fileId");
            return result;
        }
    }

    @RequestMapping(value = "/getRate", method = RequestMethod.GET)
    @ResponseBody
    public Double getRateOfGeneration(HttpServletRequest request){
        if(request.getSession(false) != null && request.getSession(false).getAttribute("fileId") != null){
            return generatorService.getRateOfGeneration(request.getSession().getAttribute("fileId").toString());
        }else{
            return Double.NaN;
        }
    }

    @RequestMapping(value = "createSession",method = RequestMethod.GET)
    @ResponseBody
    public void createSession(HttpServletRequest request){
        request.getSession(true);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(@RequestParam("fileName") String fileName, HttpServletResponse response){
        File file = new File(Utils.getMoviesDir() + fileName);
        if(!file.exists()){
            try{
                System.out.println("文件不存在！");
                response.setContentType("application/text");
                response.getWriter().println("file doesn't exists!");
            }catch (IOException e){
                e.printStackTrace();
            }
            return;
        }
        downloadFile(response, file);
    }

    /**
     * 下载文件
     * @param response response
     * @param file 文件
     * @return 返回结果 成功或者文件不存在
     */
    public static String downloadFile(HttpServletResponse response, File file) {
        File path;
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(file.getName(), "UTF-8"));
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os;
        try {
            os = response.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(file));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (FileNotFoundException e1) {
            //e1.getMessage()+"系统找不到指定的文件";
            return "系统找不到指定的文件";
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "success";
    }


}
