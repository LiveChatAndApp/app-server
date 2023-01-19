package cn.wildfirechat.app.enums;

/**
 * 预设好友类型模式枚举
 */
public enum DefaultMemberDefaultTypeEnum {

    FRIEND(1, "好友"),

    GROUP(2, "群");

    private final int value;
    private final String message;

    DefaultMemberDefaultTypeEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static DefaultMemberDefaultTypeEnum parse(Integer value) {
        if (value != null) {
            for (DefaultMemberDefaultTypeEnum info : values()) {
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
