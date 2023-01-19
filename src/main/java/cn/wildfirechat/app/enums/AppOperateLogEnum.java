package cn.wildfirechat.app.enums;

import org.springframework.data.util.Pair;

/**
 * 前台操作日誌类型
 */
public enum AppOperateLogEnum {

    LOGIN(1000L, "用户登录", "/login", true),//前台
    LOGIN_FAIL(1001L, "用户登录失败", "/login/fail", true),//前台
    LOGOUT(1002L, "用户登出", "/logout", true);

    private final Long key;
    private final String name;
    private final String api;
    private final boolean isLog;

    AppOperateLogEnum(Long key, String name, String api, boolean isLog) {
        this.key = key;
        this.name = name;
        this.api = api;
        this.isLog = isLog;
    }

    public Long getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getApi() {
        return api;
    }

    public boolean getIsLog() {
        return isLog;
    }

    public static AppOperateLogEnum parseByKey(Long key) {
        if (key != null) {
            for (AppOperateLogEnum info : values()) {
                if (info.key == key) {
                    return info;
                }
            }
        }
        return null;
    }

    public static AppOperateLogEnum parseByApi(String api) {
        if (api != null) {
            for (AppOperateLogEnum info : values()) {
                if (info.api == api) {
                    return info;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "key: "+ key + "|" +
                "name: "+ name + "|"+
                "api: "+ api + "|" +
                "isLog: "+ isLog;
    }

}
