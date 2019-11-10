package com.yzchnb.dynamicbarvideogenerator.GeneratorUtils;

import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.GeneratorConfiguation;
import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Frame;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageGenerator {
    private GeneratorConfiguation generatorConfiguation;
    private UserInputConfiguration userInputConfiguration;
    public ImageGenerator(GeneratorConfiguation generatorConfiguation){
        this.generatorConfiguation = generatorConfiguation;
        this.userInputConfiguration = generatorConfiguation.getUserInputConfiguration();
    }
    public BufferedImage generateImage(Frame frame) {
        //DUMMY
        BufferedImage bufferedImage = new BufferedImage(
                userInputConfiguration.getWidth(),
                userInputConfiguration.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0,0, userInputConfiguration.getWidth(), userInputConfiguration.getHeight());
        graphics.dispose();
        return bufferedImage;
    }
}
