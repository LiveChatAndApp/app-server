package cn.wildfirechat.app.enums;

/**
 * 用户馀额日志类型
 */
public enum MemberBalanceLogTypeEnum {
    MANUAL_RECHARGE(1, "手动充币"),
    MANUAL_WITHDRAW(2, "手动提取"),
    RECHARGE(3, "充值"),
    WITHDRAW(4, "提现"),
    TRANSFER(5, "转帐"),
    RED_ENVELOPE_SEND(6, "发红包"),
    RED_ENVELOPE_RECEIVE(7, "收红包"),
    RED_ENVELOPE_REFUND(8, "红包退款"),
    CHECK_IN_REWARD(9, "签到奖励"),
    WITHDRAW_REFUSED(10, "提现拒绝反款"),
    WITHDRAW_APPLY(11, "提现申请");

    private final int value;
    private final String message;

    MemberBalanceLogTypeEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static MemberBalanceLogTypeEnum parse(Integer value) {
        if (value != null) {
            for (MemberBalanceLogTypeEnum info : values()) {
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
