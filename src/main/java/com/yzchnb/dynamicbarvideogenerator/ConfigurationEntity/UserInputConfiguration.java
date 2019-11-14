package com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Component
@Scope("prototype")
public class UserInputConfiguration implements Serializable {
    private int width;
    private int height;
    private int FPS;
    private int DPS;
    private int numOfBarsInChart;
    private int bufferedTime;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFPS() {
        return FPS;
    }

    public void setFPS(int FPS) {
        this.FPS = FPS;
    }

    public int getDPS() {
        return DPS;
    }

    public void setDPS(int DPS) {
        this.DPS = DPS;
    }

    public int getNumOfBarsInChart() {
        return numOfBarsInChart;
    }

    public void setNumOfBarsInChart(int numOfBarsInChart) {
        this.numOfBarsInChart = numOfBarsInChart;
    }
}
