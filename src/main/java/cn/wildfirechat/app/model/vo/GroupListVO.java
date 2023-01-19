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
@ApiModel(value = "群组")
public class GroupListVO implements Serializable {

	@ApiModelProperty(value = "GID")
	private String gid;

	@ApiModelProperty(value = "群组名称")
	private String groupName;

	@ApiModelProperty(value = "头像")
	private String portrait;
}
