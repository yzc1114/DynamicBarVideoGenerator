package com.yzchnb.dynamicbarvideogenerator.GeneratorUtils;

import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.GeneratorConfiguation;
import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.UserInputConfiguration;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Bar;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Frame;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Line;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TransitionFrameGenerator {

    private GeneratorConfiguation generatorConfiguation;

    private HashMap<String, Double> lastType2Position = null;

    public TransitionFrameGenerator(GeneratorConfiguation generatorConfiguation){
        this.generatorConfiguation = generatorConfiguation;
    }

    public Frame generateFrame(Frame lastFrame, Line currLine){
        //TODO 增加配置选项 加速度
        double aInc = 0.05;
        double aDec = 0.5;
        double waitingTime = 10;
        //决定出加速和减速的加速度


        double t = 1.0 / generatorConfiguation.getUserInputConfiguration().getFPS();
        HashMap<String, Double> targetType2Position = getType2Position(currLine, generatorConfiguation.getUserInputConfiguration().getNumOfBarsInChart());
        HashMap<String, Integer> targetType2Value = currLine.getType2Value();
        ArrayList<Bar> bars = new ArrayList<>(targetType2Position.size());
        targetType2Value.forEach((type, value) ->
                bars.add(new Bar(type, value))
        );
        if(lastFrame == null){
            bars.forEach((bar -> {
                bar.setSpeed(0.0);
                bar.setPosition(targetType2Position.get(bar.getTypeName()));
            }));
            Frame frame = new Frame(bars);
            frame.calPeekDegree();
            frame.setTimeStr(currLine.getLocalDateTime().toString());
            return frame;
        }
        List<Bar> lastBars = lastFrame.getBars();
        HashMap<String, Double> lastBarType2Speed = new HashMap<>();
        HashMap<String, Double> lastBarType2Position = new HashMap<>();
        HashMap<String, Double> lastBarType2WaitingTime = new HashMap<>();
        for (Bar lastBar : lastBars) {
            lastBarType2Speed.put(lastBar.getTypeName(), lastBar.getSpeed());
            lastBarType2Position.put(lastBar.getTypeName(), lastBar.getPosition());
            lastBarType2WaitingTime.put(lastBar.getTypeName(), lastBar.getWaitingTime());
        }
        for (Bar bar: bars) {
            //计算出新的bar的position
            //通过使用加速度，以及之前的速度。
            //每个bar在进行加速前需要等待
            if(lastBarType2Speed.get(bar.getTypeName()) == 0.0){
                bar.setWaitingTime(lastBarType2WaitingTime.get(bar.getTypeName()) + t);
                if(bar.getWaitingTime() < waitingTime * t){
                    bar.setPosition(lastBarType2Position.get(bar.getTypeName()));
                    continue;
                }
                bar.setWaitingTime(0.0);
            }
            double x1 = targetType2Position.get(bar.getTypeName());
            double x0 = lastBarType2Position.get(bar.getTypeName());
            double v0 = lastBarType2Speed.get(bar.getTypeName());
            double v1;
            double position;
            if (x1 == x0) {
                bar.setSpeed(0.0);
                bar.setPosition(lastBarType2Position.get(bar.getTypeName()));
                continue;
            }
            if ((v0 >= 0 && (x1 - x0) >= 0) || (v0 <= 0 && (x1 - x0) <= 0)) {
                double distanceOfStopping = (Math.pow(v0, 2) / (2.0 * aInc * t));
                if (distanceOfStopping <= Math.abs(x1 - x0)) {
                    //刹得住车，还可以加速
                    v1 = v0 == 0 ? ((x1 - x0) > 0 ? (v0 + aInc * t) : (v0 - aInc * t))  : ((v0 < 0) ? v0 - aInc * t : v0 + aInc * t);
                    double movingDistance = (Math.pow(v0, 2) - Math.pow(v1, 2)) / (2.0 * aInc * t);
                    position = (v0 < 0) ? x0 + movingDistance : x0 - movingDistance;
                    bar.setSpeed(v1);
                    bar.setPosition(position);
                    continue;
                }
            }
            //反向或必须得刹车，必减速
            v1 = (v0 < 0) ? v0 + aDec * t : v0 - aDec * t;
            double movingDistance = (Math.pow(v0, 2) - Math.pow(v1, 2)) / (2.0 * aDec * t);
            //在一次移动内超过了目的地。需要拉回来。
            if(Math.abs(x1 - x0) < movingDistance){
                bar.setSpeed(0.0);
                bar.setPosition(x1);
                continue;
            }
            position = (v0 < 0) ? x0 - movingDistance : x0 + movingDistance;
            bar.setSpeed(v1);
            bar.setPosition(position);
        }
        Frame frame = new Frame(bars);
        frame.setTimeStr(currLine.getLocalDateTime().toString());
        frame.calPeekDegree();
        return frame;
    }

//    public ArrayList<Frame> generateFrames(ArrayList<Line> lines){
//        UserInputConfiguration userInputConfiguration = generatorConfiguation.getUserInputConfiguration();
//        int FPS = userInputConfiguration.getFPS();
//        double Tic = userInputConfiguration.getTic();
//        int numOfSortingFrames = (int)(Tic * FPS);
//        if(numOfSortingFrames >= lines.size()){
//            numOfSortingFrames = lines.size();
//        }
//        List<Line> sortingLines = lines.subList(0, numOfSortingFrames);
//        List<Line> unsortingLines = lines.subList(numOfSortingFrames, lines.size());
//        ArrayList<Frame> result = generateSortingFrames(sortingLines);
//        result.addAll(generateUnsortingFrames(unsortingLines));
//        return result;
//    }
//
//    private ArrayList<Frame> generateSortingFrames(List<Line> lines){
//        ArrayList<Frame> frames = new ArrayList<>(lines.size());
//        int numOfBarsInChart = generatorConfiguation.getUserInputConfiguration().getNumOfBarsInChart();
//        if(lines.size() <= 1){
//            return frames;
//        }
//        //分析第一行，和最后一行，得到两个line的数据。
//        //
//        Line firstLine = lines.get(0);
//        Line lastLine = lines.get(lines.size() - 1);
//
//        HashMap<String, Double> firstLineType2Position;
//        if(lastType2Position == null){
//            firstLineType2Position = getType2Position(firstLine, numOfBarsInChart);
//        }else{
//            firstLineType2Position = lastType2Position;
//        }
//        HashMap<String, Double> lastLineType2Position = getType2Position(lastLine, numOfBarsInChart);
//        lastType2Position = lastLineType2Position;
//
//        //根据一头一尾的line的position数据，分配每个bar的位置。
//        for (int i = 0; i < lines.size(); i++) {
//            Line line = lines.get(i);
//            HashMap<String, Integer> type2Value = line.getType2Value();
//            ArrayList<Bar> bars = new ArrayList<>(type2Value.size());
//            type2Value.forEach((type, value) ->
//                    bars.add(new Bar(type, value))
//            );
//            Integer index = i;
//            bars.forEach((bar) -> {
//                Double beginningPosition = firstLineType2Position.get(bar.getTypeName());
//                Double endingPosition = lastLineType2Position.get(bar.getTypeName());
//                Double step = (endingPosition - beginningPosition) / lines.size();
//                bar.setPosition(beginningPosition + step * index);
//            });
//
//            Frame frame = new Frame(bars);
//            frame.calPeekDegree();
//            frames.add(frame);
//        }
//        return frames;
//    }
//
//    private ArrayList<Frame> generateUnsortingFrames(List<Line> lines){
//        ArrayList<Frame> frames = new ArrayList<>(lines.size());
//        //int numOfBarsInChart = generatorConfiguation.getUserInputConfiguration().getNumOfBarsInChart();
//        if(lines.size() == 0){
//            return frames;
//        }
//        //分析第一行得到line的数据，然后得到type的排名，之后的position全部按照那个来。
//        //Line firstLine = lines.get(0);
//
//        for (int i = 0; i < lines.size(); i++) {
//            Line line = lines.get(i);
//            HashMap<String, Integer> type2Value = line.getType2Value();
//            ArrayList<Bar> bars = new ArrayList<>(type2Value.size());
//            type2Value.forEach((type, value) ->
//                bars.add(new Bar(type, value))
//            );
//            bars.forEach((bar) -> {
//                bar.setPosition(lastType2Position.get(bar.getTypeName()));
//            });
//
//            Frame frame = new Frame(bars);
//            frame.calPeekDegree();
//            frames.add(frame);
//        }
//        return frames;
//    }

    private HashMap<String, Double> getType2Position(Line line, int numOfBarsInChart){
        HashMap<String, Double> type2Position = new HashMap<>();
        HashMap<String, Integer> firstLineType2Value = line.getType2Value();
        List<Bar> firstBars = new ArrayList<>(firstLineType2Value.size());
        firstLineType2Value.forEach((type, value) -> firstBars.add(new Bar(type, value)));
        firstBars.sort(Comparator.comparingInt((bar) ->
            -bar.getValue()
        ));
        for (int i = 0; i < firstBars.size(); i++) {
            if(i < numOfBarsInChart){
                firstBars.get(i).setPosition((double)i / numOfBarsInChart);
            }else{
                firstBars.get(i).setPosition(1.2);
            }
        }
        firstBars.forEach((bar) ->
            type2Position.put(bar.getTypeName(), bar.getPosition())
        );
        return type2Position;
    }
}
