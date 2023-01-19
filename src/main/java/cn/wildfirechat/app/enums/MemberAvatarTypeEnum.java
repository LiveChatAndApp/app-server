package cn.wildfirechat.app.enums;

/**
 * 头像类型
 */
public enum MemberAvatarTypeEnum {
    PRESET(1, "系统默认"),
    RANDOM(2, "随机生成");

    private final int value;
    private final String message;

    MemberAvatarTypeEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static MemberAvatarTypeEnum parse(Integer value) {
        if (value != null) {
            for (MemberAvatarTypeEnum info : values()) {
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
