package cn.wildfirechat.app.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyInfoVO implements Serializable {
	private String uid; // UID
	private String memberName; // 帐号
	private String nickName; // 呢称
	private Integer gender; // 性别
	private String avatar; // 头贴
	private String mobile; // 手机号
	private String balance; // 馀额
	private Integer hasTradePwd; // 是否已设置交易密码 0:未设置 1:已设置
	private boolean isCreateGroupEnable; // 是否可建群
}
