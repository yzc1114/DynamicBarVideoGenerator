package com.yzchnb.dynamicbarvideogenerator.GeneratorUtils;

import com.yzchnb.dynamicbarvideogenerator.ConfigurationEntity.GeneratorConfiguration;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Bar;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Frame;
import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Line;

import java.util.*;

public class TransitionFrameGenerator {

    private GeneratorConfiguration generatorConfiguration;

    private HashMap<String, Double> lastType2Position = null;

    public TransitionFrameGenerator(GeneratorConfiguration generatorConfiguration){
        this.generatorConfiguration = generatorConfiguration;
    }

//    public Frame generateFrame(Frame lastFrame, Line currLine){
//        //TODO 增加配置选项 加速度
//        double aInc = 0.15;
//        double aDec = 0.5;
//        double waitingTime = 10;
//        //决定出加速和减速的加速度
//
//
//        double t = 1.0 / generatorConfiguation.getUserInputConfiguration().getFPS();
//        HashMap<String, Double> targetType2Position = getType2Position(currLine, generatorConfiguation.getUserInputConfiguration().getNumOfBarsInChart());
//        HashMap<String, Integer> targetType2Value = currLine.getType2Value();
//        ArrayList<Bar> bars = new ArrayList<>(targetType2Position.size());
//        targetType2Value.forEach((type, value) ->
//                bars.add(new Bar(type, value))
//        );
//        if(lastFrame == null){
//            bars.forEach((bar -> {
//                bar.setSpeed(0.0);
//                bar.setPosition(targetType2Position.get(bar.getTypeName()));
//            }));
//            Frame frame = new Frame(bars);
//            frame.calPeekDegree();
//            frame.setTimeStr(currLine.getLocalDateTime().toString());
//            return frame;
//        }
//        List<Bar> lastBars = lastFrame.getBars();
//        HashMap<String, Double> lastBarType2Speed = new HashMap<>();
//        HashMap<String, Double> lastBarType2Position = new HashMap<>();
//        HashMap<String, Double> lastBarType2WaitingTime = new HashMap<>();
//        for (Bar lastBar : lastBars) {
//            lastBarType2Speed.put(lastBar.getTypeName(), lastBar.getSpeed());
//            lastBarType2Position.put(lastBar.getTypeName(), lastBar.getPosition());
//            lastBarType2WaitingTime.put(lastBar.getTypeName(), lastBar.getWaitingTime());
//        }
//        for (Bar bar: bars) {
//            //计算出新的bar的position
//            //通过使用加速度，以及之前的速度。
//            //每个bar在进行加速前需要等待
//            if(lastBarType2Speed.get(bar.getTypeName()) == 0.0){
//                bar.setWaitingTime(lastBarType2WaitingTime.get(bar.getTypeName()) + t);
//                if(bar.getWaitingTime() < waitingTime * t){
//                    bar.setPosition(lastBarType2Position.get(bar.getTypeName()));
//                    continue;
//                }
//                bar.setWaitingTime(0.0);
//            }
//            double x1 = targetType2Position.get(bar.getTypeName());
//            double x0 = lastBarType2Position.get(bar.getTypeName());
//            double v0 = lastBarType2Speed.get(bar.getTypeName());
//            double v1;
//            double position;
//            if (x1 == x0) {
//                bar.setSpeed(0.0);
//                bar.setPosition(lastBarType2Position.get(bar.getTypeName()));
//                continue;
//            }
//            if ((v0 >= 0 && (x1 - x0) >= 0) || (v0 <= 0 && (x1 - x0) <= 0)) {
//                double distanceOfStopping = (Math.pow(v0, 2) / (2.0 * aInc * t));
//                if (distanceOfStopping <= Math.abs(x1 - x0)) {
//                    //刹得住车，还可以加速
//                    v1 = v0 == 0 ? ((x1 - x0) > 0 ? (v0 + aInc * t) : (v0 - aInc * t))  : ((v0 < 0) ? v0 - aInc * t : v0 + aInc * t);
//                    double movingDistance = (Math.pow(v0, 2) - Math.pow(v1, 2)) / (2.0 * aInc * t);
//                    position = (v0 < 0) ? x0 + movingDistance : x0 - movingDistance;
//                    bar.setSpeed(v1);
//                    bar.setPosition(position);
//                    continue;
//                }
//            }
//            //反向或必须得刹车，必减速
//            v1 = (v0 < 0) ? v0 + aDec * t : v0 - aDec * t;
//            double movingDistance = (Math.pow(v0, 2) - Math.pow(v1, 2)) / (2.0 * aDec * t);
//            //在一次移动内超过了目的地。需要拉回来。
//            if(Math.abs(x1 - x0) < movingDistance){
//                bar.setSpeed(0.0);
//                bar.setPosition(x1);
//                continue;
//            }
//            position = (v0 < 0) ? x0 - movingDistance : x0 + movingDistance;
//            bar.setSpeed(v1);
//            bar.setPosition(position);
//        }
//        Frame frame = new Frame(bars);
//        frame.setTimeStr(currLine.getLocalDateTime().toString());
//        frame.calPeekDegree();
//        return frame;
//    }

    public Frame generateFrame(Frame lastFrame, Line currLine){
        //TODO 增加配置选项 加速度
        double aInc = 0.15;
        double aDec = 0.5;
        double waitingTime = generatorConfiguration.getUserInputConfiguration().getBufferedFrameCount();
        //决定出加速和减速的加速度


        double t = 1.0 / generatorConfiguration.getUserInputConfiguration().getFPS();
        HashMap<String, Double> targetType2Position = getType2Position(currLine, generatorConfiguration.getUserInputConfiguration().getNumOfBarsInChart());
        HashMap<Double, String> targetPosition2Type = new HashMap<>();
        targetType2Position.forEach((type, position) -> targetPosition2Type.put(position, type));
        HashMap<String, Integer> targetType2Value = currLine.getType2Value();
        ArrayList<Bar> bars = new ArrayList<>(targetType2Position.size());
        targetType2Value.forEach((type, value) ->
                bars.add(new Bar(type, value))
        );
        if(lastFrame == null){
            bars.forEach(bar -> {
                bar.setSpeed(0.0);
                bar.setPosition(targetType2Position.get(bar.getTypeName()));
                bar.setTargetPosition(targetType2Position.get(bar.getTypeName()));
                bar.setMoving(false);
                bar.setCurrMovingTargetPosition(null);
            });
            Frame frame = new Frame(bars);
            frame.calPeekDegree();
            frame.setTimeStr(currLine.getLocalDateTime().toString());
            return frame;
        }
        List<Bar> lastBarsList = lastFrame.getBars();
        ArrayList<HashSet<String>> lastVisiteds = lastFrame.getVisiteds();
        ArrayList<HashSet<String>> currVisiteds = new ArrayList<>();

        HashMap<String, Bar> lastBars = new HashMap<>();
        for (Bar lastBar: lastBarsList) {
            lastBars.put(lastBar.getTypeName(), lastBar);
        }
//        HashMap<String, Double> lastBarType2Speed = new HashMap<>();
//        HashMap<String, Double> lastBarType2Position = new HashMap<>();
//        HashMap<String, Double> lastBarType2WaitingTime = new HashMap<>();
//        HashMap<String, Boolean> lastBarType2IsMoving = new HashMap<>();
//        HashMap<String, Boolean> lastBarType2JustStartedMoving = new HashMap<>();
//        HashMap<String, Double> lastBarType2TargetPosition = new HashMap<>();
        HashMap<Double, String> lastBarTargetPosition2BarType = new HashMap<>();
//        HashMap<String, Double> lastBarType2CurrMovingTargetPosition = new HashMap<>();
        HashMap<String, Bar> currBars = new HashMap<>();
        for(Bar bar : bars){
            //先记录当前排序好的目标位置。
            bar.setTargetPosition(targetType2Position.get(bar.getTypeName()));
            //首先设置位置与上次的位置相同。
            bar.setPosition(lastBars.get(bar.getTypeName()).getPosition());
            currBars.put(bar.getTypeName(), bar);
        }
        for (Bar lastBar : lastBarsList) {
//            lastBarType2Speed.put(lastBar.getTypeName(), lastBar.getSpeed());
//            lastBarType2Position.put(lastBar.getTypeName(), lastBar.getPosition());
//            lastBarType2WaitingTime.put(lastBar.getTypeName(), lastBar.getWaitingTime());
//            lastBarType2TargetPosition.put(lastBar.getTypeName(), lastBar.getTargetPosition());
            lastBarTargetPosition2BarType.put(lastBar.getTargetPosition(), lastBar.getTypeName());
//            lastBarType2IsMoving.put(lastBar.getTypeName(), lastBar.isMoving());
//            lastBarType2JustStartedMoving.put(lastBar.getTypeName(), lastBar.isJustStartedMoving());
//            lastBarType2CurrMovingTargetPosition.put(lastBar.getTypeName(), lastBar.getCurrMovingTargetPosition());
        }
        for (Bar bar: bars) {
            //计算出新的bar的position
            //通过使用加速度，以及之前的速度。
            //每个bar在进行加速前需要等待
            Bar lastBar = lastBars.get(bar.getTypeName());

            //目标位置 x1
            double x1 = -10;
            //初始位置 x0
            double x0 = lastBar.getPosition();
            //初始速度 v0
            double v0 = lastBar.getSpeed();
            //末速度
            double v1;
            //末位置
            double position;

            if(lastBar.isMoving()){
                //正在移动时，移动的目标就设置为上次的目标位置。
                //x1 = lastBarType2CurrMovingTargetPosition.get(bar.getTypeName());
                x1 = lastBar.getCurrMovingTargetPosition();
                bar.setCurrMovingTargetPosition(x1);
                bar.setJustStartedMoving(false);
                bar.setMoving(true);
            }else{
                /*
                //没有移动时，检测当前的目标位置是否和上次的目标位置相同。
                //此处定义Bar类的waitingTime语义为，等待同一个目标位置的时间。
                //若相同，则waitingTime增加，若增加到一定量，则开始移动。
                //若不相同，则waitingTime重置，之后continue*/
//                if(lastBarType2TargetPosition.get(bar.getTypeName()).equals(targetType2Position.get(bar.getTypeName()))){
//                    bar.setWaitingTime(lastBarType2WaitingTime.get(bar.getTypeName()) + t);
//                    if(bar.getWaitingTime() >= waitingTime * t){
//                        bar.setWaitingTime(0.0);
//                        bar.setMoving(true);
//                        bar.setCurrMovingTargetPosition(targetType2Position.get(bar.getTypeName()));
//                        x1 = targetType2Position.get(bar.getTypeName());
//                    }else{
//                        bar.setPosition(lastBarType2Position.get(bar.getTypeName()));
//                        bar.setMoving(false);
//                        bar.setCurrMovingTargetPosition(null);
//                        continue;
//                    }
//                }else{
//                    bar.setWaitingTime(0.0);
//                    bar.setMoving(false);
//                    bar.setPosition(lastBarType2Position.get(bar.getTypeName()));
//                    bar.setCurrMovingTargetPosition(null);
//                    continue;
//                }
                /**
                 * 上述注释的算法不给力。重新思考
                 * 我们需要对数据重叠这一事实进行避免。最佳的方案是，任意一个想要换位置的bar，
                 * 都与其它的bar达成协议，即，每个bar都同时有自己的想去的位置，并且达成一种循环。
                 * 那么只需要在每帧处去查看自己的目的位置，有没有形成循环。
                 * 若形成循环，则开始移动，并且途中不可以被打断。
                 * 下面的算法若能在循环中找到自己，则证明可以开始移动了。
                 */

                //TODO 修bug！！！！！
                if(lastBar.getPosition().equals(bar.getTargetPosition())){
                    //bar.setPosition(lastBarType2Position.get(bar.getTypeName()));
                    bar.setPosition(lastBar.getPosition());
                    bar.stop();
                    continue;
                }
                boolean needCal;
                String lastBarType = bar.getTypeName();
                String currBarType;
                //visited集合中，存储了协议中的全部type
                HashSet<String> visited = new HashSet<>();
                while(true){
                    if(visited.contains(lastBarType)){
                        needCal = false;
                        break;
                    }else{
                        visited.add(lastBarType);
                    }
                    currBarType = targetPosition2Type.get(lastBars.get(lastBarType).getPosition());
                    if(lastBarType.equals(currBarType)){
                        needCal = false;
                        break;
                    }
                    if(currBarType == null){
                        needCal = false;
                        break;
                    }
                    if(!currBarType.equals(bar.getTypeName())){
                        if(currBars.get(currBarType).isMoving() && !currBars.get(currBarType).isJustStartedMoving()){
                            //当循环中的一个环节是正在移动中，并且不是刚刚开始移动的，则不能进行移动。
                            needCal = false;
                            break;
                        }else{
                            lastBarType = currBarType;
                        }
                    }else{
                        //找到了循环结尾，和自己相同。
                        //该位置是唯一需要进行计算地出口处。
                        //visited集合存储了这一循环中的全部typeName
                        //去和上一帧中的visited做对比，如果有一致的，则bar的waitingTime增加
                        //若增加到一定量，则开始移动，并将visited
                        //否则，waitingTime重置。
                        boolean movable = false;
                        boolean foundSame = false;
                        currVisiteds.add(visited);
                        for (HashSet<String> lastVisited : lastVisiteds) {
                            if(lastVisited.equals(visited)){
                                foundSame = true;
                                bar.setWaitingTime(lastBar.getWaitingTime() + t);
                                if(bar.getWaitingTime() >= waitingTime * t){
                                    movable = true;
                                }
                                break;
                            }
                        }
                        if(!foundSame){
                            bar.setWaitingTime(0.0);
                            needCal = false;
                            break;
                        }
                        if(movable){
                            bar.setWaitingTime(0.0);
                            bar.setMoving(true);
                            bar.setJustStartedMoving(true);
                            x1 = targetType2Position.get(bar.getTypeName());
                            bar.setCurrMovingTargetPosition(x1);
                            needCal = true;
                            break;
                        }else{
                            needCal = false;
                            break;
                        }

                        //13个会有bug
//                        bar.setWaitingTime(0.0);
//                        bar.setMoving(true);
//                        bar.setJustStartedMoving(true);
//                        x1 = targetType2Position.get(bar.getTypeName());
//                        bar.setCurrMovingTargetPosition(x1);
//                        needCal = true;
//                        break;
                    }
                }
                if(!needCal){
                    //bar.setPosition(lastBarType2Position.get(bar.getTypeName()));
                    bar.setPosition(lastBar.getPosition());
                    bar.stop();
                    continue;
                }

            }
//            if(lastBarType2Speed.get(bar.getTypeName()) == 0.0){
//                bar.setWaitingTime(lastBarType2WaitingTime.get(bar.getTypeName()) + t);
//                if(bar.getWaitingTime() < waitingTime * t){
//                    bar.setPosition(lastBarType2Position.get(bar.getTypeName()));
//                    continue;
//                }
//                bar.setWaitingTime(0.0);
//            }
//            double x1 = targetType2Position.get(bar.getTypeName());
//            double x0 = lastBarType2Position.get(bar.getTypeName());
//            double v0 = lastBarType2Speed.get(bar.getTypeName());
//            double v1;
//            double position;
            //运行到此，说明bar正在移动中或正在开始移动，我们需要进一步确定位置
            assert x1 != -10;

            if (x1 == x0) {
                //bar.setPosition(lastBarType2Position.get(bar.getTypeName()));
                bar.setPosition(lastBar.getPosition());
                bar.stop();
                continue;
            }
            if ((v0 >= 0 && (x1 - x0) >= 0) || (v0 <= 0 && (x1 - x0) <= 0)) {
                double distanceOfStopping = (Math.pow(v0, 2) / (2.0 * aDec * t));
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
                bar.setPosition(x1);
                bar.stop();
                continue;
            }
            position = (v0 < 0) ? x0 - movingDistance : x0 + movingDistance;
            bar.setSpeed(v1);
            bar.setPosition(position);
            if(position == x1){
                bar.stop();
            }
        }
        //
        Frame frame = new Frame(bars);
        frame.setTimeStr(currLine.getLocalDateTime().toString());
        frame.setVisiteds(currVisiteds);
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
                firstBars.get(i).setPosition(1.2 + i * 1e-7);
            }
        }
        firstBars.forEach((bar) ->
            type2Position.put(bar.getTypeName(), bar.getPosition())
        );
        return type2Position;
    }
}
