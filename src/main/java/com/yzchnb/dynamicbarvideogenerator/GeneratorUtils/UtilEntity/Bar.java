package com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity;


import java.awt.*;

public class Bar {
    private String typeName;
    private Integer value;
    private Double position;
    private Color color;
    private Double speed;
    private Double waitingTime;
    //语义：在本帧中的排序位置
    private Double targetPosition;
    //语义：在移动时的目标位置。
    private Double currMovingTargetPosition;
    //是否正在移动
    private boolean moving;
    //是否刚刚开始移动
    private boolean justStartedMoving;
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

    public Double getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(Double targetPosition) {
        this.targetPosition = targetPosition;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public Double getCurrMovingTargetPosition() {
        return currMovingTargetPosition;
    }

    public void setCurrMovingTargetPosition(Double currMovingTargetPosition) {
        this.currMovingTargetPosition = currMovingTargetPosition;
    }

    public boolean isJustStartedMoving() {
        return justStartedMoving;
    }

    public void setJustStartedMoving(boolean justStartedMoving) {
        this.justStartedMoving = justStartedMoving;
    }

    public Bar(String typeName, Integer value){
        this.typeName = typeName;
        this.value = value;
        this.color=color_array[Math.abs(this.typeName.hashCode())%color_array.length];
        this.speed = 0.0;
        this.waitingTime = 0.0;
        this.targetPosition = null;
        this.moving = false;
        this.justStartedMoving = false;
    }

    public void stop(){
        this.speed = 0.0;
        this.moving = false;
        this.currMovingTargetPosition = null;
        this.justStartedMoving = false;
    }
}
