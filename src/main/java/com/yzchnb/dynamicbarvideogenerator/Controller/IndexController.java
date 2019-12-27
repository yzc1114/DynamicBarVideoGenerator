package com.yzchnb.dynamicbarvideogenerator.Controller;

import com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity.GeneratorConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Logger.Logger;
import com.yzchnb.dynamicbarvideogenerator.Service.IGeneratorService;
import com.yzchnb.dynamicbarvideogenerator.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;

@RestController
public class IndexController {
    @Autowired
    private IGeneratorService generatorService;

    /**
     * 检查参数和数据文件
     * @param userInputConfiguration
     * @param multipartFile
     * @return
     */
    @RequestMapping(value = "/checkParams", method = RequestMethod.POST, consumes = {MediaType.ALL_VALUE})
    @ResponseBody
    public String checkParams(@ModelAttribute UserInputConfiguration userInputConfiguration,
                                @RequestParam(value = "file") MultipartFile multipartFile) throws Exception{
        GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.from(userInputConfiguration);
        if(multipartFile.isEmpty()){
            String error = "文件上传失败";
            Logger.log(LocalDateTime.now().toString() + error);
            throw new Exception(error);
        }
        File tempFile = new File(Utils.getDataFileDir() + userInputConfiguration.hashCode() + ".csv");

        try{
            multipartFile.transferTo(tempFile);
        }catch (Exception e){
            e.printStackTrace();
            String error = "文件转换失败";
            Logger.log(error);
            throw new Exception(error);
        }
        String result;
        //上传csv文件
        try{
            generatorService.checkParams(generatorConfiguration, tempFile);
            result = "success";
        }catch (Exception e){
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    /**
     * 生成视频
     * @param userInputConfiguration
     * @param multipartFile
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/generateVideo", method = RequestMethod.POST, consumes = {MediaType.ALL_VALUE})
    @ResponseBody
    public String generateVideo(@ModelAttribute UserInputConfiguration userInputConfiguration,
                                @RequestParam(value = "file") MultipartFile multipartFile,
                                HttpServletRequest request) throws Exception{
        if(request.getSession(false) != null){
            if(request.getSession(false).getAttribute("fileId") != null){
                return null;
            }
        }
        request.getSession(true).setAttribute("fileId", userInputConfiguration.hashCode());

        GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.from(userInputConfiguration);
        if(multipartFile.isEmpty()){
            String error = "文件上传失败";
            Logger.log(error);
            throw new Exception(error);
        }
        File tempFile = new File(Utils.getDataFileDir() + userInputConfiguration.hashCode() + ".csv");

        try{
            multipartFile.transferTo(tempFile);
        }catch (Exception e){
            e.printStackTrace();
            String error = "文件转换失败";
            Logger.log(error);
            throw new Exception(error);
        }
        String result;
        //上传csv文件
        try{
             result = generatorService.generateVideo(generatorConfiguration, tempFile, Utils.getMoviesDir(),
                    request.getSession().getAttribute("fileId").toString());

        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            request.getSession(true).removeAttribute("fileId");
        }
        return result;
    }

    /**
     * 获取生成率
     * @param request
     * @return
     */
    @RequestMapping(value = "/getRate", method = RequestMethod.GET)
    @ResponseBody
    public Double getRateOfGeneration(HttpServletRequest request){
        if(request.getSession(false) != null && request.getSession(false).getAttribute("fileId") != null){
            return generatorService.getRateOfGeneration(request.getSession().getAttribute("fileId").toString());
        }else{
            return Double.NaN;
        }
    }

    /**
     * 创建会话
     * @param request
     */
    @RequestMapping(value = "createSession",method = RequestMethod.GET)
    @ResponseBody
    public void createSession(HttpServletRequest request){
        request.getSession(true);
    }

    /**
     * 下载视频接口
     * @param fileName
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(@RequestParam("fileName") String fileName, HttpServletResponse response) throws Exception{
        File file = new File(Utils.getMoviesDir() + fileName);
        if(!file.exists()){
            throw new Exception("文件不存在！");
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
