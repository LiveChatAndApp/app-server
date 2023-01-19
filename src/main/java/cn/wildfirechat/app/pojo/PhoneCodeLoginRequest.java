package cn.wildfirechat.app.pojo;

import lombok.Data;

@Data
public class PhoneCodeLoginRequest {
    private String mobile;
    private String code;
    private String inviteCode;
    private String clientId;
    private Integer platform;

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
