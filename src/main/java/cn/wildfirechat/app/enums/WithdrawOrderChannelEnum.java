package cn.wildfirechat.app.enums;

/**
 * 提现订单渠道
 */
public enum WithdrawOrderChannelEnum {
    BANK_CARD(1, "银行卡"),
    WECHAT(2, "微信"),
    ALIPAY(3, "支付宝");

    private final int value;
    private final String message;

    WithdrawOrderChannelEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static WithdrawOrderChannelEnum parse(Integer value) {
        if (value != null) {
            for (WithdrawOrderChannelEnum info : values()) {
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
