package cn.wildfirechat.app.enums;

import java.util.Arrays;

public enum StatusBasicEnum {
    CLOSE(0),
    OPEN(1);

    private Integer value;

    StatusBasicEnum(Integer value){
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static boolean isClose(Integer value) {
        return CLOSE.equals(parse(value));
    }

    public static boolean isOpen(Integer value) {
        return OPEN.equals(parse(value));
    }

    public static StatusBasicEnum parse(Integer value) {
        if (value != null) {
            for (StatusBasicEnum info : values()) {
                if (info.value == value) {
                    return info;
                }
            }
        }
        return null;
    }
}
