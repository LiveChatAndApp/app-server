package cn.wildfirechat.app.enums;

/**
 * 邀请码状态枚举
 */
public enum InviteCodeStatusEnum {

    DELETE(0, "删除"),

    OPEN(1, "使用中"),

    CLOSE(2, "停用");

    private final int value;
    private final String message;

    InviteCodeStatusEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static InviteCodeStatusEnum parse(Integer value) {
        if (value != null) {
            for (InviteCodeStatusEnum info : values()) {
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
