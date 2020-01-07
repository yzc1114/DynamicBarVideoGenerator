package com.yzchnb.dynamicbarvideogenerator.DataProcessor;

import com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity.GeneratorConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Entity.GeneratorEntity.Line;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineProvider {
    static Line boxLine(Line lastLine, String lineStr, List<String> types) throws Exception{
        String[] values = lineStr.split(",");
        if(values.length != types.size() + 1){
            throw new Exception("数据列数不够");
        }
        Line line = new Line();

        setLocalDateTime(line, values[0]);

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
        ParamsChecker.checkParams(generatorConfiguration,csvFile);


        UserInputConfiguration userInputConfiguration = generatorConfiguration.getUserInputConfiguration();
        int DPS = userInputConfiguration.getDPS();
        int FPS = userInputConfiguration.getFPS();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFile));
        long linesCount = bufferedReader.lines().count();
        bufferedReader = new BufferedReader(new FileReader(csvFile));
        String firstLine = bufferedReader.readLine();
        String[] splitedfirstLine = firstLine.split(",");


        ArrayList<String> types = new ArrayList<>(splitedfirstLine.length - 1);
        for (int i = 1; i < splitedfirstLine.length; i++) {
            types.add(splitedfirstLine[i]);
        }

        ArrayList<Line> lines = new ArrayList<>((int)linesCount);
        int frameCount;
        Line lastLine = null;
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
                lastLine = boxLine(lastLine, lineStr, types);
                lines.add(lastLine);
            }
        }else{
            while(true){
                String lineStr = bufferedReader.readLine();
                //do not skip lines
                if(lineStr == null){
                    break;
                }
                lastLine = boxLine(lastLine, lineStr, types);
                //add same line for multiple times
                for (int i = 0; i < FPS / DPS; i++) {
                    lines.add(lastLine);
                }
            }
        }
        return lines;
    }

    private static void setLocalDateTime(Line line, String timeStr) throws Exception{
        timeStr = timeStr.trim();
        String[] supported_formats = {"yyyy-mm-dd", "yyyy-mm", "yyyy"};
        String yyyymmdd = "^(\\d{4})-(\\d{2})-(\\d{2})$";
        String yyyymm = "^(\\d{4})-(\\d{2})$";
        String yyyy = "^(\\d{4})$";
        Pattern pyyyymmdd = Pattern.compile(yyyymmdd);
        Pattern pyyyymm = Pattern.compile(yyyymm);
        Pattern pyyyy = Pattern.compile(yyyy);
        Pattern[] patterns = {pyyyymmdd, pyyyymm, pyyyy};
        for(Pattern pattern : patterns){
            Matcher matcher = pattern.matcher(timeStr);
            if(matcher.find()){
                if(pattern == pyyyymmdd){
                    line.setTimeFormat(Line.TimeFormat.YYYY_MM_DD);
                    int year = Integer.parseInt(matcher.group(1));
                    int month = Integer.parseInt(matcher.group(2));
                    int day = Integer.parseInt(matcher.group(3));
                    line.setLocalDate(LocalDate.of(year, month, day));
                }
                if(pattern == pyyyymm){
                    int year = Integer.parseInt(matcher.group(1));
                    int month = Integer.parseInt(matcher.group(2));
                    line.setLocalDate(LocalDate.of(year, month, 1));
                    line.setTimeFormat(Line.TimeFormat.YYYY_MM);
                }
                if(pattern == pyyyy){
                    int year = Integer.parseInt(matcher.group(1));
                    line.setLocalDate(LocalDate.of(year, 1, 1));
                    line.setTimeFormat(Line.TimeFormat.YYYY);
                }

            }
        }
        if(line.getLocalDate() == null){
            //没有支持的时间格式
            throw new Exception("没有支持的时间格式！");
        }
    }

}
