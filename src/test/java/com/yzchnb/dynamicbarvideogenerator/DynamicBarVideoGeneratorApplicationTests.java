package com.yzchnb.dynamicbarvideogenerator;

import com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity.GeneratorConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.Service.IGeneratorService;
import com.yzchnb.dynamicbarvideogenerator.Utils.Utils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;

@SpringBootTest
class DynamicBarVideoGeneratorApplicationTests {

    @Resource
    private IGeneratorService generatorService;

    private File data;

    @Test
    void contextLoads() {
        data = new File("test.csv");
        assert data.exists();
    }

    @Test
    void testWorkFlow() throws Exception{
        contextLoads();
        UserInputConfiguration uic = new UserInputConfiguration();
        uic.setTitle("test");
        uic.setHeight(360);
        uic.setWidth(480);
        uic.setBufferedFrameCount(5);
        uic.setDPS(90);
        uic.setFPS(30);
        uic.setNumOfBarsInChart(10);
        uic.setPredict(false);
        GeneratorConfiguration generatorConfiguration = GeneratorConfiguration.from(uic);
        generatorService.checkParams(generatorConfiguration, data);
        String moviePath = generatorService.generateVideo(generatorConfiguration, data, Utils.getMoviesDir(), "testFileId");
        System.setOut(System.out);
        System.out.println(moviePath);
    }


}
