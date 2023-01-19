package cn.wildfirechat.app.enums;

public enum HttpRequestEnum {
    GENERAL(60),
    THIRD_GENERAL(10),
    THIRD_CATCH(5);

    private Integer seconds;

    HttpRequestEnum(Integer seconds){
        this.seconds = seconds;
    }

    public Integer getSeconds() {
        return seconds;
    }
}
