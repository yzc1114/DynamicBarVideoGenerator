package com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity;


import java.awt.*;

public class Bar {
    private String typeName;
    private Integer value;
    private Double position;
    private Color color;
    private Double speed;
    private Double waitingTime;
    private static Color[] color_array={Color.RED,Color.BLUE,Color.GREEN,Color.YELLOW,Color.PINK,Color.CYAN, Color.ORANGE, Color.MAGENTA};
    public String getTypeName() {
        return typeName;
    }

    public Integer getValue() {
        return value;
    }

    public Double getPosition() {
        return position;
    }

    public Color getColor(){
        return color;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(Double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setPosition(Double position) {
        this.position = position;
    }

    public Bar(String typeName, Integer value){
        this.typeName = typeName;
        this.value = value;
        this.color=color_array[Math.abs(this.typeName.hashCode())%color_array.length];
        this.speed = 0.0;
        this.waitingTime = 0.0;
    }
}
