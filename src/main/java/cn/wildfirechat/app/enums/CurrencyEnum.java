package cn.wildfirechat.app.enums;

/**
 * 币种枚举
 */
public enum CurrencyEnum {

//    HKD(344, "港元"),
//    IDR(360, "印尼盾"),
//    THB(764, "泰铢"),
//    GBP(826, "英鎊"),
//    USD(840, "美元"),
//    EUR(978, "歐元"),
    CNY(156, "人民幣元");

    private final Integer value;// 数字代码

    private final String message;// 货币名称


    CurrencyEnum(Integer value, String message) {
        this.value = value;
        this.message = message;
    }

    public Integer getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static CurrencyEnum parse(String code) {
        if (code != null) {
            for (CurrencyEnum info : values()) {
                if (info.name().equals(code)) {
                    return info;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name() + "|" + value + "|" + message;
    }
}
