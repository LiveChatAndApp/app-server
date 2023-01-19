package cn.wildfirechat.app.enums;

/**
 * 预设好友模式枚举
 */
public enum InviteCodeDefaultFriendTypeEnum {

    ALL(1, "所有"),

    LOOP(2, "轮询");

    private final int value;
    private final String message;

    InviteCodeDefaultFriendTypeEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static InviteCodeDefaultFriendTypeEnum parse(Integer value) {
        if (value != null) {
            for (InviteCodeDefaultFriendTypeEnum info : values()) {
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
