package cn.wildfirechat.app.model.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RespondFriendInviteForm implements Serializable {
    @ApiModelProperty(value = "uid")
    private String uid;

    @ApiModelProperty(value = "0:拒絕 1:接受")
    private Integer reply;

    @ApiModelProperty(value = "验证讯息")
    private String verifyText;
}
