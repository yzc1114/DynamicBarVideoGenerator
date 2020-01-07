package com.yzchnb.dynamicbarvideogenerator.DataProcessor;

import com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity.GeneratorConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Entity.GeneratorEntity.Line;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParamsChecker {
    public static void checkParams(GeneratorConfiguration generatorConfiguration, File csvFile) throws Exception{
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
            if(userInputConfiguration.getNumOfBarsInChart() <= 0){
                throw new Exception("Num of Bars In Chart 必须大于0");
            }
            if(userInputConfiguration.getBufferedFrameCount() < 0){
                throw new Exception("Buffered Frame Count 必须大于等于0");
            }
            if(userInputConfiguration.getHeight() <= 0){
                throw new Exception("Height 必须大于0");
            }
            if(userInputConfiguration.getWidth() <= 0){
                throw new Exception("Width 必须大于0");
            }
            int DPS = userInputConfiguration.getDPS();
            if(DPS <= 0){
                throw new Exception("DPS 必须大于0");
            }
            int FPS = userInputConfiguration.getFPS();
            if(FPS <= 0){
                throw new Exception("FPS 必须大于0");
            }
            if(DPS % FPS != 0 && FPS % DPS != 0){
                throw new Exception("DPS 与 FPS 不是整数倍关系！");
            }

            if(splitedfirstLine.length <= 1){
                throw new Exception("输入文件格式不对：列太少");
            }

            List<String> types = Arrays.asList(splitedfirstLine);
            if(types.size() == 1){
                throw new Exception("数据只有1列，请检查您的数据！");
            }
            types = types.subList(1, types.size());
            Line lastLine = null;
            while(true){
                String lineStr = bufferedReader.readLine();
                if(lineStr == null){
                    break;
                }
                lastLine = LineProvider.boxLine(lastLine, lineStr, types);
            }

        }catch (IOException e){
            e.printStackTrace();
            throw e;
        }catch (Exception e){
            throw e;
        }
    }

}
