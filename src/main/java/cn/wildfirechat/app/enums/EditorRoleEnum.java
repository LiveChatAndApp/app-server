package cn.wildfirechat.app.enums;

/**
 * 编辑者角色 1: 系统管理者, 2: 会员枚举
 */
public enum EditorRoleEnum {

    ADMIN(1, "系统管理者"),

    MEMBER(2, "会员");

    private final int value;

    private final String message;

    EditorRoleEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static EditorRoleEnum parse(Integer value) {
        if (value != null) {
            for (EditorRoleEnum info : values()) {
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
