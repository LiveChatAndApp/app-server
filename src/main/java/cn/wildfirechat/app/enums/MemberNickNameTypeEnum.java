package cn.wildfirechat.app.enums;

/**
 * 昵称类型
 */
public enum MemberNickNameTypeEnum {
    RANDOM(1, "随机昵称"),
    DEFINITION(2, "自定义昵称");

    private final int value;
    private final String message;

    MemberNickNameTypeEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static MemberNickNameTypeEnum parse(Integer value) {
        if (value != null) {
            for (MemberNickNameTypeEnum info : values()) {
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
