package cn.wildfirechat.app.enums;

/**
 * 充值订单充值方式
 */
public enum RechargeOrderMethodEnum {
    BANK_CARD(1, "银行卡"),
    WECHAT(2, "微信"),
    ALIPAY(3, "支付宝");

    private final int value;
    private final String message;

    RechargeOrderMethodEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static RechargeOrderMethodEnum parse(Integer value) {
        if (value != null) {
            for (RechargeOrderMethodEnum info : values()) {
                if (info.value == value) {
                    return info;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value + "|" + message;
    }
}
