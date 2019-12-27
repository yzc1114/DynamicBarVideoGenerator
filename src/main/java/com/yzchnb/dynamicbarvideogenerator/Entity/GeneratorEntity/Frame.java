package com.yzchnb.dynamicbarvideogenerator.Entity.GeneratorEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class Frame {
    private List<Bar> bars;
    private ArrayList<HashSet<String>> visiteds = new ArrayList<>();
    private Double peekValue;
    private Integer peekDegree;
    private Integer baseDegree;
    private String timeStr;

    public String getTimeStr(){
        return timeStr;
    }
    public List<Bar> getBars() {
        return bars;
    }
    public Integer getBaseDegree(){
        return baseDegree;
    }
    public Double getPeekValue() {
        return peekValue;
    }
    public Integer getPeekDegree(){
        return peekDegree;
    }

    public Frame(List<Bar> bars) {
        this.bars = bars;
        Double peekValue = Double.MIN_VALUE;
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
        this.baseDegree=0;
    }

    public void calPeekDegree() {
        bars.sort(Comparator.comparingDouble((bar) ->
            -bar.getPosition()
        ));
        double min_v = Double.MAX_VALUE;
        double max_v = Double.MIN_VALUE;
        for(Bar bar :bars){
            if(bar.getPosition()<=1){
                min_v = Math.min(min_v, bar.getValue());
                max_v = Math.max(max_v, bar.getValue());
            }
        }
        peekDegree = (int)(max_v*5/4);
        baseDegree = 0;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public ArrayList<HashSet<String>> getVisiteds() {
        return visiteds;
    }

    public void setVisiteds(ArrayList<HashSet<String>> visiteds) {
        this.visiteds = visiteds;
    }
}
