package cn.wildfirechat.app.enums;

/**
 * 好友/群验证状态枚举
 * 验证 0: 待同意, 1: 需要验证讯息, 2: 成功, 3: 失败
 */
public enum RelateVerifyEnum {

    PENDING(0, "待同意"),
    VERIFY(1, "需要验证讯息"),

    SUCCESS(2, "成功"),

    FAILED(3, "失败");


    private final int value;
    private final String message;

    RelateVerifyEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static RelateVerifyEnum parse(Integer value) {
        if (value != null) {
            for (RelateVerifyEnum info : values()) {
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
