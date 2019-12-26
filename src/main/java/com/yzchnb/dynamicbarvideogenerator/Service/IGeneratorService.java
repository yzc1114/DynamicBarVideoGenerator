package com.yzchnb.dynamicbarvideogenerator.Service;

import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.GeneratorConfiguration;
import java.io.File;

public interface IGeneratorService {
    String generateVideo(GeneratorConfiguration generatorConfiguration, File csvFile, String generateDir, String fileId) throws Exception;

    Double getRateOfGeneration(String fileId);

    void checkParams(GeneratorConfiguration generatorConfiguration, File csvFile) throws Exception;
}
