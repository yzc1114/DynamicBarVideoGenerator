package com.yzchnb.dynamicbarvideogenerator.Service.impls;

import com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity.GeneratorConfiguration;
import com.yzchnb.dynamicbarvideogenerator.ContextStorage.ProcessorMap;
import com.yzchnb.dynamicbarvideogenerator.DataProcessor.LineProvider;
import com.yzchnb.dynamicbarvideogenerator.DataProcessor.ParamsChecker;
import com.yzchnb.dynamicbarvideogenerator.Generator.CenterProcessor;
import com.yzchnb.dynamicbarvideogenerator.Entity.GeneratorEntity.Line;
import com.yzchnb.dynamicbarvideogenerator.Service.IGeneratorService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;

import static com.yzchnb.dynamicbarvideogenerator.DataProcessor.DataForecaster.doPredict;

@Service
public class GeneratorService implements IGeneratorService {
    public String generateVideo(GeneratorConfiguration generatorConfiguration, File csvFile, String generateDir, String fileId) throws Exception{
        //DONE 生成视频，存到本地，返回视频url
        //DONE 读取文件内容，将types记录下来
        try{
            ArrayList<Line> lines = LineProvider.generateLines(generatorConfiguration, csvFile);
            boolean predict = generatorConfiguration.getUserInputConfiguration().isPredict();
            if(predict){
                lines = doPredict(lines, 0.1);
            }
            CenterProcessor centerController = new CenterProcessor(generatorConfiguration, lines.size(), generateDir);
            ProcessorMap.putCenterProcessor(fileId,centerController);
            for (Line line : lines) {
                centerController.consumeDataLine(line);
            }
            centerController.dispose();
            String result = "/movies/" + centerController.waitResult();
            ProcessorMap.removeCenterProcessor(fileId);
            csvFile.delete();
            return result;
        }catch (IOException e){
            e.printStackTrace();
            throw e;
        }catch (Exception e){
            throw e;
        }
    }

    public Double getRateOfGeneration(String fileId){
        if(!ProcessorMap.containsCenterProcessorKey(fileId)){
            return Double.NaN;
        }else{
            return ProcessorMap.getCenterProcessor(fileId).getRate();
        }
    }

    public void checkParams(GeneratorConfiguration generatorConfiguration, File csvFile) throws Exception {
        ParamsChecker.checkParams(generatorConfiguration, csvFile);
    }
}
