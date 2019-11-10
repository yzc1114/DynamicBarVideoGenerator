package com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity;

import java.time.LocalDateTime;
import java.util.HashMap;

public class Line {
    private LocalDateTime localDateTime;
    private HashMap<String, Integer> type2Value;

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public HashMap<String, Integer> getType2Value() {
        return type2Value;
    }

    public void setType2Value(HashMap<String, Integer> type2Value) {
        this.type2Value = type2Value;
    }
}
