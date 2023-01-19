package cn.wildfirechat.app.model.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("新增好友表单")
public class AddFriendRequestForm {

    @ApiModelProperty(value = "uid")
    private String uid;

    @ApiModelProperty(value = "是否需要验证讯息, false: 不需验证, true: 需要验证")
    private boolean verify;

    @ApiModelProperty(value = "验证讯息")
    private String verifyText;

    @ApiModelProperty(value = "打招呼讯息")
    private String helloText;
}
