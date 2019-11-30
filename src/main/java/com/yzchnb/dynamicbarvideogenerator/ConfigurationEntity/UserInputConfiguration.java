package com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope("prototype")
public class UserInputConfiguration implements Serializable {
    private int width;
    private int height;
    private int FPS;
    private int DPS;
    private int numOfBarsInChart;
    private int bufferedFrameCount;
    private boolean predict;
    private String title;

    public int getBufferedFrameCount(){
        return bufferedFrameCount;
    }
    public void setBufferedFrameCount(int bufferedFrameCount){
        this.bufferedFrameCount=bufferedFrameCount;
    }
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

    public boolean isPredict() {
        return predict;
    }

    public void setPredict(boolean predict) {
        this.predict = predict;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
