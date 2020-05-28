package com.cgcg.context.enums;

public enum PayStatusEnum {

    //0：未付款,1已付款,2:已发货,3交易成功

    WAIT(0, "待支付"),
    PAYED(1, "已支付"),
    SHIPMENT(2, "已发货"),
    SUCCESS(3, "交易成功"),
    PAYING(4, "支付中"),
    FAILED(5, "支付失败"),
    CLOSE(6, "已关闭")
    ;

    private Integer value;
    private String name;

    PayStatusEnum(Integer value, String name){
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getNameByValue(String value) {
        for(PayStatusEnum type : PayStatusEnum.values()) {
            if (type.getValue().equals(value)) {
                return type.getName();
            }
        }
        return "";
    }
}
