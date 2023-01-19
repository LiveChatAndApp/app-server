package cn.wildfirechat.app.enums;

/**
 * 性别类型
 */
public enum MemberGenderEnum {
    SECRET(1, "保密"),
    MALE(2, "男"),
    FEMALE(3, "女");

    private final int value;
    private final String message;

    MemberGenderEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static MemberGenderEnum parse(Integer value) {
        if (value != null) {
            for (MemberGenderEnum info : values()) {
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
