package cn.wildfirechat.app.enums;

/**
 * 会员帐号类型
 */
public enum MemberAccountTypeEnum {
    ORDINARY(1, "普通帐号"),
    MANAGE(2, "管理号");

    private final int value;
    private final String message;

    MemberAccountTypeEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static MemberAccountTypeEnum parse(Integer value) {
        if (value != null) {
            for (MemberAccountTypeEnum info : values()) {
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
