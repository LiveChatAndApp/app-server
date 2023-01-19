package cn.wildfirechat.app.enums;

/**
 * 群组状态枚举
 * 状态 1: 正常, 2: 解散
 */
public enum GroupStatusEnum {
    NORMAL(1, "正常"),
    DISSOLVE(2, "解散");

    private final int value;

    private final String message;

    GroupStatusEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static GroupStatusEnum parse(Integer value) {
        if (value != null) {
            for (GroupStatusEnum info : values()) {
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
