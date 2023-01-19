package cn.wildfirechat.app.jpa;

import cn.wildfirechat.app.tools.UUIDUtils;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Persistant Object 会员列表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_member")
@DynamicUpdate
public class Member {

    public static final String MEMBER_TRADE_PWD_SALT = "SX&SLC`";

    @Id
    @Column(length = 19)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;// ID

    @Column(name = "_uid")
    private String uid;// UID
    @Column(name = "_nick_name")
    private String nickName;// 昵称
    @Column(name = "_member_name")
    private String memberName;// 帐号
//    @Column(name = "_password")
//    private String password;// 密码
    @Column(name = "_trade_pwd")
    private String tradePwd;// 交易密码
    @Column(name = "_account_type")
    private Integer accountType;// 帐号类型 1:普通帐号, 2:管理号 [MemberAccountTypeEnum]
    @Column(name = "_invite_code")
    private String inviteCode;// 邀请码
    @Column(name = "_invite_member_id")
    private Long inviteMemberId;// 邀请用户ID
    @Column(name = "_avatar_url")
    private String avatarUrl;// 头像
    @Column(name = "_phone")
    private String phone;// 手机号
    @Column(name = "_email")
    private String email;// 邮箱
    @Column(name = "_signature")
    private String signature;// 个性签名
    @Column(name = "_gender")
    private Integer gender;// 性别 1: 保密, 2: 男, 3: 女 [MemberAccountTypeEnum]
    @Column(name = "_login_status")
    private Integer loginStatus;// 登陆状态
//    @Column(name = "_last_active_time")
//    private Date lastActiveTime;// 最后活跃时间
//    @Column(name = "_login_error_count")
//    private Integer loginErrorCount;// 登陆错误次数
    @Column(name = "_register_ip")
    private String registerIp;// 注册IP
    @Column(name = "_register_area")
    private String registerArea;// 注册地区
    @Column(name = "_balance")
    private BigDecimal balance;// 馀额
    @Column(name = "_login_enable")
    private Boolean loginEnable;// 登陆启用
    @Column(name = "_add_friend_enable")
    private Boolean addFriendEnable;// 添加好友开关
    @Column(name = "_create_group_enable")
    private Boolean createGroupEnable;// 建群开关
    @Column(name = "_admin_enable")
    private Boolean adminEnable;// 管理号开关
    @Column(name = "_qr_code_token")
    private String qrCodeToken;// QRCode验证
    @Column(name = "_channel")
    private String channel;// 渠道
    @Column(name = "_memo")
    private String memo;// 备注
    @Column(name = "_create_time", columnDefinition="DATETIME DEFAULT CURRENT_TIMESTAMP",insertable = false,updatable = false)
    private Date createTime;// 创建时间
    @Column(name = "_update_time", columnDefinition="DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP",insertable = false,updatable = false)
    private Date updateTime;// 最后修改时间

    @OneToMany(fetch = FetchType.LAZY, targetEntity = MemberBalance.class, mappedBy = "userId")
    private List<MemberBalance> memberBalanceList;

    public static String createUid() {
//        return String.format("B%s", DateUtils.format(new Date(), DateUtils.YMDHMSSS));
        return String.format("B%s", UUIDUtils.shortUUID(16).replaceAll("-", ""));
    }
}
