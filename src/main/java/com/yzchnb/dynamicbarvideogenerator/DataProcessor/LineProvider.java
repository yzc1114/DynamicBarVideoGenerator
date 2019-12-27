package com.yzchnb.dynamicbarvideogenerator.DataProcessor;

import com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity.GeneratorConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Entity.GeneratorEntity.Line;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class LineProvider {
    public static Line boxLine(String lineStr, ArrayList<String> types) throws Exception{
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
        HashMap<String, Double> type2Value = new HashMap<>();
        for (int i = 1; i < values.length; i++) {
            try{
                type2Value.put(types.get(i-1), Double.parseDouble(values[i]));
            }catch (NumberFormatException e){
                throw new Exception("某数据列不是整数！");
            }
        }
        line.setType2Value(type2Value);
        return line;
    }

    public static ArrayList<Line> generateLines(GeneratorConfiguration generatorConfiguration, File csvFile) throws Exception{
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
        return lines;
    }

}
