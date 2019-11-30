package com.yzchnb.dynamicbarvideogenerator.Service;

import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.GeneratorConfiguration;
import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.CenterProcessor;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Line;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeneratorService {
    private ConcurrentHashMap<String,CenterProcessor> processorMap=new ConcurrentHashMap<>();
    public String generateVideo(GeneratorConfiguration generatorConfiguration, File csvFile, String generateDir, String fileId) throws Exception{
        //DONE 生成视频，存到本地，返回视频url
        //DONE 读取文件内容，将types记录下来
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFile));
            long linesCount = bufferedReader.lines().count();
            if(linesCount <= 2){
                throw new Exception("数据太少！");
            }
            bufferedReader = new BufferedReader(new FileReader(csvFile));
            String firstLine = bufferedReader.readLine();
            String[] splitedfirstLine = firstLine.split(",");

            UserInputConfiguration userInputConfiguration = generatorConfiguration.getUserInputConfiguration();
            int DPS = userInputConfiguration.getDPS();
            int FPS = userInputConfiguration.getFPS();
            if(DPS % FPS != 0 && FPS % DPS != 0){
                throw new Exception("DPS 与 FPS 不是整数倍关系！");
            }

            if(splitedfirstLine.length <= 1){
                throw new Exception("输入文件格式不对：列太少");
            }

            ArrayList<String> types = new ArrayList<>(splitedfirstLine.length - 1);
            for (int i = 1; i < splitedfirstLine.length; i++) {
                types.add(splitedfirstLine[i]);
            }

            ArrayList<Line> lines = new ArrayList<>((int)linesCount);
            int frameCount;
            if(DPS > FPS){
                int skipNum = DPS / FPS - 1;

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
                    lines.add(line);
                }
            }else{
                while(true){
                    String lineStr = bufferedReader.readLine();
                    //do not skip lines
                    if(lineStr == null){
                        break;
                    }
                    Line line = boxLine(lineStr, types);
                    //add same line for multiple times
                    for (int i = 0; i < FPS / DPS; i++) {
                        lines.add(line);
                    }
                }
            }

            boolean predict = userInputConfiguration.isPredict();
            if(predict){
                lines = doPredict(lines, 0.1);
            }

            frameCount = lines.size();

            CenterProcessor centerController = new CenterProcessor(generatorConfiguration, types, frameCount, generateDir);
            processorMap.put(fileId,centerController);

            for (Line line : lines) {
                centerController.consumeDataLine(line);
            }
            centerController.dispose();

            String result = "/movies/" + centerController.waitResult();
            processorMap.remove(fileId);
            return result;

        }catch (IOException e){
            e.printStackTrace();
            throw e;
        }catch (Exception e){
            throw e;
        }finally {
            csvFile.delete();
        }
    }

    public void checkParams(GeneratorConfiguration generatorConfiguration, File csvFile) throws Exception{
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFile));
            long linesCount = bufferedReader.lines().count();
            if(linesCount <= 2){
                throw new Exception("数据太少！");
            }
            bufferedReader = new BufferedReader(new FileReader(csvFile));
            String firstLine = bufferedReader.readLine();
            String[] splitedfirstLine = firstLine.split(",");

            UserInputConfiguration userInputConfiguration = generatorConfiguration.getUserInputConfiguration();
            int DPS = userInputConfiguration.getDPS();
            int FPS = userInputConfiguration.getFPS();
            if(DPS % FPS != 0 && FPS % DPS != 0){
                throw new Exception("DPS 与 FPS 不是整数倍关系！");
            }

            if(splitedfirstLine.length <= 1){
                throw new Exception("输入文件格式不对：列太少");
            }

            ArrayList<String> types = new ArrayList<>(splitedfirstLine.length - 1);
            for (int i = 1; i < splitedfirstLine.length; i++) {
                types.add(splitedfirstLine[i]);
            }

            Line lastLine = null;
            Long lastDuration = null;
            while(true){
                String lineStr = bufferedReader.readLine();
                if(lineStr == null){
                    break;
                }
                Line newLine = boxLine(lineStr, types);
                if(lastLine != null){
                    if(lastDuration != null){
                        long newDuration = Duration.between(lastLine.getLocalDateTime(), newLine.getLocalDateTime()).toNanos();
                        if(newDuration != lastDuration){
                            throw new Exception("时间间隔不同！");
                        }
                        lastDuration = newDuration;
                    }else{
                        lastDuration = Duration.between(lastLine.getLocalDateTime(), newLine.getLocalDateTime()).toNanos();
                    }
                    lastLine = newLine;
                }else{
                    lastLine = boxLine(lineStr, types);
                }

            }

        }catch (IOException e){
            e.printStackTrace();
            throw e;
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

        ArrayList<String> supportedFormats = new ArrayList<String>() {{
            add("yyyy-MM-dd HH:mm:ss"); add("yyyy-MM-dd"); add("yyyy-MM"); add("yyyy");
        }};

        for(String format : supportedFormats){
            try{
                line.setLocalDateTime(LocalDateTime.parse(values[0], DateTimeFormatter.ofPattern(format)));
            }catch (DateTimeParseException e){
                //try again
            }
        }
        if(line.getLocalDateTime() == null){
            //没有支持的时间格式
            throw new Exception("没有支持的时间格式！");
        }
        HashMap<String, Integer> type2Value = new HashMap<>();
        for (int i = 1; i < values.length; i++) {
            try{
                type2Value.put(types.get(i-1), Integer.parseInt(values[i]));
            }catch (NumberFormatException e){
                throw new Exception("某数据列不是整数！");
            }
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

    private ArrayList<Line> doPredict(ArrayList<Line> lines, double proportion){
        assert proportion < 1;
        ArrayList<Line> newLines = (ArrayList<Line>) lines.clone();

        Line fLine = newLines.get(0);
        Line lLine = newLines.get(newLines.size() - 1);
        int length = newLines.size();
        Duration d = Duration.between(fLine.getLocalDateTime(), lLine.getLocalDateTime());
        long sepNanos = d.toNanos() / newLines.size();
        LocalDateTime lLineTime = lLine.getLocalDateTime();

        HashMap<String, Integer> fLineType2Value = fLine.getType2Value();
        HashMap<String, Integer> lLineType2Value = lLine.getType2Value();
        HashMap<String, Double> type2Grad = new HashMap<>();
        fLineType2Value.forEach((k, v) -> type2Grad.put(k, (lLineType2Value.get(k) - v) / (1.0 * length)));

        for (int i = 0; i < length * proportion; i++){
            Line newLine = new Line();

            newLine.setType2Value((HashMap<String, Integer>) lLineType2Value.clone());

            newLine.getType2Value().forEach((k, v) -> {
                Random r = new Random();
                double ratio = (r.nextInt(60) - 30) / 100.0;
                int newValue = v + (int)((1 + ratio) * type2Grad.get(k));
                newLine.getType2Value().put(k, newValue);
            });

            lLineType2Value.clear();
            lLineType2Value.putAll(newLine.getType2Value());

            lLineTime = lLineTime.plusNanos(sepNanos);
            newLine.setLocalDateTime(lLineTime);
            newLines.add(newLine);
        }
        return newLines;
    }
}
