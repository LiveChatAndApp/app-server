package cn.wildfirechat.app.enums;

/**
 * 好友 邀請接收枚举
 * 验证 0:拒絕 1:接受
 */
public enum RelateReplyEnum {

    REJECT(0, "拒絕"),
    ACCEPT(1, "接受");


    private final int value;
    private final String message;

    RelateReplyEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static RelateReplyEnum parse(Integer value) {
        if (value != null) {
            for (RelateReplyEnum info : values()) {
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
