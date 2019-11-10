package com.yzchnb.dynamicbarvideogenerator.GeneratorUtils.UtilEntity;


public class Bar {
    private String typeName;
    private Integer value;
    private Double position;

    public String getTypeName() {
        return typeName;
    }

    public Integer getValue() {
        return value;
    }

    public Double getPosition() {
        return position;
    }

    public void setPosition(Double position) {
        this.position = position;
    }

    public Bar(String typeName, Integer value){
        this.typeName = typeName;
        this.value = value;
    }
}
