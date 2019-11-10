package com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity;

import java.util.ArrayList;
import java.util.List;

public class Frame {
    private List<Bar> bars;
    private Integer peekValue;
    private Integer peekDegree;
    public List<Bar> getBars() {
        return bars;
    }

    public Integer getPeekValue() {
        return peekValue;
    }
    public Integer getPeekDegree(){
        return peekDegree;
    }

    public Frame(List<Bar> bars) {
        this.bars = bars;
        int peekValue = Integer.MIN_VALUE;
        for (Bar bar: bars) {
            if(bar.getValue() > peekValue){
                peekValue = bar.getValue();
            }
        }
        //TODO 将最大值通过某种规则转换成标尺最大值。
        if(peekValue == 0){
            this.peekDegree = 10;
            return;
        }
        int count = 0;
        while(peekValue / ((int)Math.pow(10, count)) != 0){
            count += 1;
        }
        this.peekDegree = (int) (Math.ceil(peekValue / Math.pow(10, count)) * Math.pow(10, count));
        this.peekValue = peekValue;
    }
}
