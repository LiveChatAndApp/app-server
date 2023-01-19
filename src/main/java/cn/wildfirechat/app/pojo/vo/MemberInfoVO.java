package cn.wildfirechat.app.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoVO implements Serializable {
    private Long id;// ID
    private String uid;// UID
    private String nickName;// 昵称
    private String memberName;// 帐号
    private Integer accountType;// 帐号类型 1:普通帐号, 2:管理号 [MemberAccountTypeEnum]
    private String inviteCode;// 邀请码
    private Long inviteMemberId;// 邀请用户ID
    private String avatarUrl;// 头像
    private String phone;// 手机号
    private String email;// 邮箱
    private String signature;// 个性签名
    private Integer gender;// 性别 1: 保密, 2: 男, 3: 女 [MemberAccountTypeEnum]
    private Integer loginStatus;// 登陆状态
    private String registerIp;// 注册IP
    private String registerArea;// 注册地区
    private Boolean loginEnable;// 登陆启用
    private Boolean addFriendEnable;// 添加好友开关
    private Boolean createGroupEnable;// 建群开关
    private Boolean adminEnable;// 管理号开关
    private String qrCodeToken;// QRCode验证
    private String memo;// 备注
}
