package cn.wildfirechat.app.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UserInfoRequest {
	@ApiModelProperty(value = "本名")
	private String name;
	@ApiModelProperty(value = "呢称")
	private String nickName;
	@ApiModelProperty(value = "头像")
	private MultipartFile avatar;
	@ApiModelProperty(value = "性别 1: 保密, 2: 男, 3:女")
	private Integer gender;
	@ApiModelProperty(value = "电话")
	private String phone;
	@ApiModelProperty(value = "email")
	private String email;
	@ApiModelProperty(value = "地址")
	private String address;
	@ApiModelProperty(value = "公司")
	private String company;
	@ApiModelProperty(value = "社交信息")
	private String social;
	@ApiModelProperty(value = "extra信息")
	private String extra;
}
