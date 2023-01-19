package cn.wildfirechat.app.enums;

import java.util.Arrays;

/**
 * 提现订单状态
 */
public enum WithdrawOrderStatusEnum {
    PENDING_REVIEW(1, "待审核"),
    COMPLETED(2, "已完成"),
    REJECTED(3, "已拒绝"),
    USER_CANCEL(4, "用户取消");

    private final int value;
    private final String message;

    WithdrawOrderStatusEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static boolean isAudit(Integer value) {
        WithdrawOrderStatusEnum statusEnum = parse(value);
        return Arrays.asList(PENDING_REVIEW).contains(statusEnum);
    }

    public static WithdrawOrderStatusEnum parse(Integer value) {
        if (value != null) {
            for (WithdrawOrderStatusEnum info : values()) {
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
