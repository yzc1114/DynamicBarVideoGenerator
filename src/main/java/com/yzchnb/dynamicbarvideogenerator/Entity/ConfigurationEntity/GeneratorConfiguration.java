package com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope("prototype")
public class GeneratorConfiguration {
    private UserInputConfiguration userInputConfiguration;
    private double widthOfBarChart;
    private double heightOfBarChart;
    private double marginHorizontal;
    private double marginVertical;
    private double originX;
    private double originY;
    private double widthOfBar;
    private String dirPath;

    public UserInputConfiguration getUserInputConfiguration() {
        return userInputConfiguration;
    }

    public static GeneratorConfiguration from(UserInputConfiguration u){
        return new GeneratorConfiguration(u);
    }

    public double getWidthOfBarChart() {
        return widthOfBarChart;
    }

    public double getHeightOfBarChart() {
        return heightOfBarChart;
    }

    public double getMarginHorizontal() {
        return marginHorizontal;
    }

    public double getMarginVertical() {
        return marginVertical;
    }

    public double getOriginX() {
        return originX;
    }

    public double getOriginY() {
        return originY;
    }

    public double getWidthOfBar() {
        return widthOfBar;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    private ArrayList<String> types;
    public void setTypes(ArrayList<String> types){
        this.types = types;
    }

    private GeneratorConfiguration(UserInputConfiguration userInputConfiguration){
        this.userInputConfiguration = userInputConfiguration;
        marginHorizontal = userInputConfiguration.getWidth() / 20.0;
        marginVertical = userInputConfiguration.getHeight() / 20.0;
        originX = marginHorizontal;
        originY = userInputConfiguration.getHeight() / 6;
        heightOfBarChart = userInputConfiguration.getHeight() * 5.0 / 6.0 - marginVertical;
        widthOfBarChart = userInputConfiguration.getWidth() - marginHorizontal * 2.0;
        widthOfBar = heightOfBarChart / userInputConfiguration.getNumOfBarsInChart() * ( 5.0/6.0 );
    }
}
