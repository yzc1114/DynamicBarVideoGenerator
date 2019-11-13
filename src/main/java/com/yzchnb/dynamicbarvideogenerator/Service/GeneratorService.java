package com.yzchnb.dynamicbarvideogenerator.Service;

import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.GeneratorConfiguation;
import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.CenterProcessor;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Line;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeneratorService {
    private ConcurrentHashMap<String,CenterProcessor> processorMap=new ConcurrentHashMap<>();
    public String generateVideo(GeneratorConfiguation generatorConfiguation, File csvFile, String generateDir, String fileId) throws Exception{
        //TODO 生成视频，存到本地，返回视频url
        //TODO 读取文件内容，将types记录下来
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFile));
            long linesCount = bufferedReader.lines().count();
            if(linesCount <= 2){
                throw new Exception("数据太少");
            }
            bufferedReader = new BufferedReader(new FileReader(csvFile));
            String firstLine = bufferedReader.readLine();
            String[] splitedfirstLine = firstLine.split(",");

            UserInputConfiguration userInputConfiguration = generatorConfiguation.getUserInputConfiguration();
            int DPS = userInputConfiguration.getDPS();
            int FPS = userInputConfiguration.getFPS();
            if(DPS % FPS != 0){
                throw new Exception("DPS 不能整除 FPS");
            }
            int frameCount = (int)(linesCount / (DPS / FPS));
            int skipNum = DPS / FPS - 1;

            if(splitedfirstLine.length <= 1){
                throw new Exception("输入文件格式不对：列太少");
            }

            ArrayList<String> types = new ArrayList<>(splitedfirstLine.length - 1);
            for (int i = 1; i < splitedfirstLine.length; i++) {
                types.add(splitedfirstLine[i]);
            }
            CenterProcessor centerController = new CenterProcessor(generatorConfiguation, types, frameCount,generateDir);
            processorMap.put(fileId,centerController);
            while(true){
                String lineStr = bufferedReader.readLine();
                //skipping lines
                for (int i = 0; i < skipNum; i++) {
                    bufferedReader.readLine();
                }
                if(lineStr == null){
                    break;
                }
                Line line = boxLine(lineStr, types);
                centerController.consumeDataLine(line);
            }
            centerController.dispose();
            String result = "/resources/movies/" +centerController.waitResult();
            processorMap.remove(fileId);
            return result;

        }catch (IOException e){
            e.printStackTrace();
            return null;
        }catch (Exception e){
            throw e;
        }
    }

    private Line boxLine(String lineStr, ArrayList<String> types) throws Exception{
        String[] values = lineStr.split(",");
        if(values.length != types.size() + 1){
            throw new Exception("数据列数不够");
        }
        Line line = new Line();
        line.setLocalDateTime(LocalDateTime.parse(values[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        HashMap<String, Integer> type2Value = new HashMap<>();
        for (int i = 1; i < values.length; i++) {
            type2Value.put(types.get(i-1), Integer.parseInt(values[i]));
        }
        line.setType2Value(type2Value);
        return line;
    }

    public Double getRateOfGeneration(String fileId){
        if(!processorMap.containsKey(fileId)){
            return Double.NaN;
        }else{
            return processorMap.get(fileId).getRate();
        }
    }
}
