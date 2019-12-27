package com.yzchnb.dynamicbarvideogenerator.Entity.GeneratorEntity;

import java.time.LocalDateTime;
import java.util.HashMap;

public class Line {
    private LocalDateTime localDateTime = null;
    private HashMap<String, Double> type2Value = null;

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public HashMap<String, Double> getType2Value() {
        return type2Value;
    }

    public void setType2Value(HashMap<String, Double> type2Value) {
        this.type2Value = type2Value;
    }
}
