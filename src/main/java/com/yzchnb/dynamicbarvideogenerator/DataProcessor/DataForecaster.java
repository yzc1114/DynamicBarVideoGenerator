package com.yzchnb.dynamicbarvideogenerator.DataProcessor;

import com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity.Line;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DataForecaster {
    public static ArrayList<Line> doPredict(ArrayList<Line> lines, double proportion){
        assert proportion < 1;
        ArrayList<Line> newLines = (ArrayList<Line>) lines.clone();

        Line fLine = newLines.get(0);
        Line lLine = newLines.get(newLines.size() - 1);
        int length = newLines.size();
        Duration d = Duration.between(fLine.getLocalDateTime(), lLine.getLocalDateTime());
        long sepNanos = d.toNanos() / newLines.size();
        LocalDateTime lLineTime = lLine.getLocalDateTime();

        HashMap<String, Integer> fLineType2Value = fLine.getType2Value();
        HashMap<String, Integer> lLineType2Value = lLine.getType2Value();
        HashMap<String, Double> type2Grad = new HashMap<>();
        fLineType2Value.forEach((k, v) -> type2Grad.put(k, (lLineType2Value.get(k) - v) / (1.0 * length)));

        for (int i = 0; i < length * proportion; i++){
            Line newLine = new Line();

            newLine.setType2Value((HashMap<String, Integer>) lLineType2Value.clone());

            newLine.getType2Value().forEach((k, v) -> {
                Random r = new Random();
                double ratio = (r.nextInt(60) - 30) / 100.0;
                int newValue = v + (int)((1 + ratio) * type2Grad.get(k));
                newLine.getType2Value().put(k, newValue);
            });

            lLineType2Value.clear();
            lLineType2Value.putAll(newLine.getType2Value());

            lLineTime = lLineTime.plusNanos(sepNanos);
            newLine.setLocalDateTime(lLineTime);
            newLines.add(newLine);
        }
        return newLines;
    }
}
