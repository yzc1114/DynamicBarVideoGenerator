package com.yzchnb.dynamicbarvideogenerator.Entity.GeneratorEntity;

import java.time.LocalDate;
import java.util.HashMap;

public class Line {
    public enum TimeFormat{
        YYYY_MM_DD("yyyy-mm-dd"),
        YYYY_MM("yyyy-mm"),
        YYYY("yyyy");

        public String format;

        TimeFormat(String s){
            format = s;
        }
    }
    private LocalDate localDateTime = null;
    private TimeFormat timeFormat = null;
    private HashMap<String, Double> type2Value = null;

    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(TimeFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    public LocalDate getLocalDate() {
        return localDateTime;
    }

    public void setLocalDate(LocalDate localDateTime) {
        this.localDateTime = localDateTime;
    }

    public HashMap<String, Double> getType2Value() {
        return type2Value;
    }

    public void setType2Value(HashMap<String, Double> type2Value) {
        this.type2Value = type2Value;
    }
}
