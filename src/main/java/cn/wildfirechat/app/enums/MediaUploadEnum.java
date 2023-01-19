package cn.wildfirechat.app.enums;

import java.util.Objects;

/**
 * 档案上传方式枚举
 */
public enum MediaUploadEnum {
    LOCAL(0, "本机"),
    QINIU(1, "七牛云");

    private final Integer value;

    private final String message;


    MediaUploadEnum(Integer value, String message) {
        this.value = value;
        this.message = message;
    }

    public Integer getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static MediaUploadEnum parse(Integer value) {
        if (value != null) {
            for (MediaUploadEnum info : values()) {
                if (Objects.equals(info.value, value)) {
                    return info;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name() + "|" + value + "|" + message;
    }
}
