package com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity;

import java.util.ArrayList;
import java.util.Comparator;
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
        this.peekValue = peekValue;
    }

    public void calPeekDegree() {
        bars.sort(Comparator.comparingDouble((bar) ->
            -bar.getPosition()
        ));
        int min_v = Integer.MAX_VALUE;
        int max_v = Integer.MIN_VALUE;
        for(Bar bar :bars){
            if(bar.getPosition()<=1){
                min_v = Math.min(min_v, bar.getValue());
                max_v = Math.max(max_v, bar.getValue());
            }
        }
        peekDegree = Math.max((max_v - min_v) * 2, peekValue);
    }
}
