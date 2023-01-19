package cn.wildfirechat.app.model.vo;

import io.swagger.annotations.ApiModel;
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
@ApiModel(value = "朋友邀请")
public class FriendRequestVO implements Serializable {

	@ApiModelProperty(value = "UID")
	private String uid;

	@ApiModelProperty(value = "昵称")
	private String nickName;

	@ApiModelProperty(value = "帐号")
	private String memberName;

	@ApiModelProperty(value = "性别 1: 保密, 2: 男, 3: 女")
	private Integer gender;

	@ApiModelProperty(value = "头贴")
	private String avatar;

	@ApiModelProperty(value = "手机号")
	private String mobile;

	@ApiModelProperty(value = "是否需要验证讯息, 0: 不需要, 1: 需要")
	private Boolean verify;

	@ApiModelProperty(value = "打招呼訊息")
	private String helloText;
}
