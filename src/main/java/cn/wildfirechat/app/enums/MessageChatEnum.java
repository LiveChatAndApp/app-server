package cn.wildfirechat.app.enums;

/**
 * 会话类型枚举
 * 会话类型 1: 单聊, 2: 群组, 3: 聊天室, 4: 频道
 */
public enum MessageChatEnum {
    UNKNOWN(0, "未知"),

    PRIVATE(1, "单聊"),

    GROUP(2, "群组"),

    CHAT(3, "聊天室"),

    CHANNEL(4, "频道"),

    SYSTEM_FRIEND_HELLO_MESSAGE(92, "系统：打招呼讯息"),
    SYSTEM_FRIEND_ALREADY_MESSAGE(93, "系统：已是好友");

    private final int value;

    private final String message;

    MessageChatEnum(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static MessageChatEnum parse(Integer value) {
        if (value != null) {
            for (MessageChatEnum info : values()) {
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
