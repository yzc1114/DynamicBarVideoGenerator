package com.yzchnb.dynamicbarvideogenerator.Entity.GeneratorEntity;


import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bar {
    private String typeName;
    private Double value;
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
    public String getTypeName() {
        return typeName;
    }

    public Double getValue() {
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

    public Bar(String typeName, Double value){
        this.typeName = typeName;
        this.value = value;
        this.color=generateColor(typeName);
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
    private static Color[] BaseColor;
    static{
        int[] base_value={200,100,50,0};
        List<Color> colorList=new ArrayList<>();
        for(int i=0;i<base_value.length;++i){
            for(int j=0;j<base_value.length;++j){
                for(int k=0;k<base_value.length;++k){
                    int r=base_value[i];
                    int g=base_value[j];
                    int b=base_value[k];
                    if(r==g&&g==b){
                        continue;
                    }else {
                        colorList.add(new Color(r,g,b));
                    }
                }
            }
        }
        BaseColor=new Color[colorList.size()];
        colorList.toArray(BaseColor);
    }
    private Color generateColor(String typeName){
        final int MAX_OFFSET=50;
        final int MAX_COLOR_DEGREE=256;
        int[] offset_rgb=new int[3];
        int hash_code=typeName.hashCode();
        for(int i=0;i<3;++i){
            offset_rgb[i]=hash_code%MAX_COLOR_DEGREE%MAX_OFFSET;
            hash_code*=hash_code;
        }
        Color base=BaseColor[(hash_code%BaseColor.length+BaseColor.length)%BaseColor.length];
        int red=(offset_rgb[0]+base.getRed()+MAX_COLOR_DEGREE)%MAX_COLOR_DEGREE;
        int green=(offset_rgb[1]+base.getGreen()+MAX_COLOR_DEGREE)%MAX_COLOR_DEGREE;
        int blue=(offset_rgb[2]+base.getBlue()+MAX_COLOR_DEGREE)%MAX_COLOR_DEGREE;
        return new Color(red,green,blue);
    }
}
