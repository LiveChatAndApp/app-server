package cn.wildfirechat.app.enums;

/**
 * 消息类型枚举
 * 消息类型 1:文本, 2: 语音, 3: 图片, 4: 文件, 5: 视频, 6: 建群, 7: 群加人, 8: 其它
 */
public enum MessageTypeEnum {

    TEXT(1, "文本"),

    AUDIO(2, "语音"),

    IMAGE(3, "图片"),

    FILE(4, "文件"),

    VIDEO(5, "视频"),

    OTHER(8, "其它");

    private final Integer value;

    private final String message;

    MessageTypeEnum(Integer value, String message) {
        this.value = value;
        this.message = message;
    }

    public Integer getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static MessageTypeEnum parse(Integer value) {
        if (value != null) {
            for (MessageTypeEnum info : values()) {
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
