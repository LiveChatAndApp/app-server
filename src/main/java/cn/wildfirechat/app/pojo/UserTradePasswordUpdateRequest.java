package cn.wildfirechat.app.pojo;

import lombok.Data;

@Data
public class UserTradePasswordUpdateRequest {
    private String newPwd;
    private String doubleCheckPwd;
    private String oldPwd;

}
