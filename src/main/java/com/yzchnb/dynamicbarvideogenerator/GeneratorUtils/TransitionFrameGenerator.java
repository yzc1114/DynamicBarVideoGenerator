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

    public TransitionFrameGenerator(GeneratorConfiguation generatorConfiguation){
        this.generatorConfiguation = generatorConfiguation;
    }

    public ArrayList<Frame> generateFrames(ArrayList<Line> lines){
        UserInputConfiguration userInputConfiguration = generatorConfiguation.getUserInputConfiguration();
        int FPS = userInputConfiguration.getFPS();
        double Tic = userInputConfiguration.getTic();
        int numOfSortingFrames = (int)(Tic * FPS);
        if(numOfSortingFrames >= lines.size()){
            numOfSortingFrames = lines.size();
        }
        List<Line> sortingLines = lines.subList(0, numOfSortingFrames);
        List<Line> unsortingLines = lines.subList(numOfSortingFrames, lines.size());
        ArrayList<Frame> result = generateSortingFrames(sortingLines);
        result.addAll(generateUnsortingFrames(unsortingLines));
        return result;
    }

    private ArrayList<Frame> generateSortingFrames(List<Line> lines){
        ArrayList<Frame> frames = new ArrayList<>(lines.size());
        int numOfBarsInChart = generatorConfiguation.getUserInputConfiguration().getNumOfBarsInChart();
        if(lines.size() <= 1){
            return frames;
        }
        //分析第一行，和最后一行，得到两个line的数据。
        //
        Line firstLine = lines.get(0);
        Line lastLine = lines.get(lines.size() - 1);
        HashMap<String, Double> firstLineType2Position = getType2Position(firstLine, numOfBarsInChart);
        HashMap<String, Double> lastLineType2Position = getType2Position(lastLine, numOfBarsInChart);

        //根据一头一尾的line的position数据，分配每个bar的位置。
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            HashMap<String, Integer> type2Value = line.getType2Value();
            ArrayList<Bar> bars = new ArrayList<>(type2Value.size());
            type2Value.forEach((type, value) ->
                    bars.add(new Bar(type, value))
            );
            Integer index = i;
            bars.forEach((bar) -> {
                Double beginningPosition = firstLineType2Position.get(bar.getTypeName());
                Double endingPosition = lastLineType2Position.get(bar.getTypeName());
                Double step = (endingPosition - beginningPosition) / lines.size();
                bar.setPosition(beginningPosition + step * index);
            });

            Frame frame = new Frame(bars);
            frame.calPeekDegree();
            frames.add(frame);
        }
        return frames;
    }

    private ArrayList<Frame> generateUnsortingFrames(List<Line> lines){
        ArrayList<Frame> frames = new ArrayList<>(lines.size());
        int numOfBarsInChart = generatorConfiguation.getUserInputConfiguration().getNumOfBarsInChart();
        if(lines.size() == 0){
            return frames;
        }
        //分析第一行得到line的数据，然后得到type的排名，之后的position全部按照那个来。
        Line firstLine = lines.get(0);
        HashMap<String, Double> type2Position = getType2Position(firstLine, numOfBarsInChart);

        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            HashMap<String, Integer> type2Value = line.getType2Value();
            ArrayList<Bar> bars = new ArrayList<>(type2Value.size());
            type2Value.forEach((type, value) ->
                bars.add(new Bar(type, value))
            );
            bars.forEach((bar) -> {
                bar.setPosition(type2Position.get(bar.getTypeName()));
            });

            Frame frame = new Frame(bars);
            frame.calPeekDegree();
            frames.add(frame);
        }
        return frames;
    }

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
                firstBars.get(i).setPosition(2.0);
            }
        }
        firstBars.forEach((bar) ->
            type2Position.put(bar.getTypeName(), bar.getPosition())
        );
        return type2Position;
    }
}
