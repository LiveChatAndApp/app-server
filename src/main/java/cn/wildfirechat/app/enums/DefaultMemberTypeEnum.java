package cn.wildfirechat.app.enums;

/**
 * 预设好友模式枚举
 */
public enum DefaultMemberTypeEnum {

    ALL(1, "所有新注册用户"),

    INVITE_CODE_ONLY(2, "使用邀请码注册用户");

    private final int value;
    private final String message;

    DefaultMemberTypeEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static DefaultMemberTypeEnum parse(Integer value) {
        if (value != null) {
            for (DefaultMemberTypeEnum info : values()) {
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
