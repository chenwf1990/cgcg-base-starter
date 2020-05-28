package com.cgcg.context.enums;

public enum RspCodeEnum {

    SUCCESS("0000", "请求成功"),
    FAILED("9999", "请求失败"),
    ;

    private String value;
    private String name;

    RspCodeEnum(String value, String name){
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getNameByValue(String value) {
        for(RspCodeEnum type : RspCodeEnum.values()) {
            if (type.getValue().equals(value)) {
                return type.getName();
            }
        }
        return "";
    }
}
