package cn.wildfirechat.app.enums;

public enum OrderTypeEnum {
    RECHARGE(1),//手動充值
    WITHDRAW(2);//手動提現

    private Integer value;

    OrderTypeEnum(Integer value){
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static OrderTypeEnum parse(Integer value) {
        if (value != null) {
            for (OrderTypeEnum info : values()) {
                if (info.value == value) {
                    return info;
                }
            }
        }
        return null;
    }
}
