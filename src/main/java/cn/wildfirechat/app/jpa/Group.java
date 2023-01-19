package cn.wildfirechat.app.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Persistant Object 邀请码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_group")
public class Group {

    @Id
    @Column(length = 19)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;// ID

    @Column(name = "_gid")
    private String gid; // 群组ID

    @Column(name = "_name")
    private String name; // 群名称(群信息)

    @Column(name = "_manager_id")
    private Long managerId; // 群主

//    @Column(name = "_member_count_limit")
//    private Integer memberCount;// 群人数

    @Column(name = "_member_count_limit")
    private Integer memberCountLimit; // 群人数上限

    @Column(name = "_group_image")
    private String groupImage; // 群头像图片

    @Column(name = "_mute_type")
    private Integer muteType; // 禁言 0:正常 1:禁言普通成员 2:禁言整个群 [GroupMuteTypeEnum]

    @Column(name = "_enter_auth_type")
    private Integer enterAuthType; // 入群验证类型 1:无需验证 2:管理员验证 3:不允许入群验证 [GroupEnterAuthTypeEnum]

    @Column(name = "_invite_permission")
    private Integer invetePermission; // 邀请权限(谁可以邀请他人入群) 1:管理员 2:所有人 [GroupInvitePermissionEnum]

    @Column(name = "_invite_auth")
    private Integer inviteAuth; // 被邀请人身份验证 0:不需要同意 1:需要同意 [GroupInviteAuthEnum]

    @Column(name = "_modify_permission")
    private Integer modifyPermission; // 群资料修改权限 1:管理员 2:所有人 [GroupModifyPermissionEnum]

    @Column(name = "_private_chat")
    private Integer privateChat; // 私聊 1: 正常, 2: 禁止 [GroupModifyPermissionEnum]

    @Column(name = "_bulletin_title")
    private Integer bulletinTitle; // 群公告标题

    @Column(name = "_bulletin_content")
    private Integer bulletinContent; // 群公告内容

    @Column(name = "_status")
    private Integer status; // 状态 1: 正常, 2: 解散 [GroupStatusEnum]

    @Column(name = "_group_type")
    private Integer groupType; // 群组类型 1: 一般, 2: 广播 [GroupTypeEnum]

    @Column(name = "_create_time")
    private Date createTime;// 创建时间

    @Column(name = "_update_time")
    private Date updateTime;// 最后修改时间

    @Column(name = "_creator")
    private String creator; // 创建者

    @Column(name = "_updater")
    private String updater; // 修改者

    @Column(name = "_creator_role")
    private Integer creatorRole; // 创建者角色 1: 系统管理者, 2: 会员 [EditorRoleEnum]

    @Column(name = "_updater_role")
    private Integer updaterRole; // 修改者角色 1: 系统管理者, 2: 会员 [EditorRoleEnum]


    @OneToMany(fetch = FetchType.LAZY, targetEntity = DefaultMember.class, mappedBy = "inviteCodeId")
    private List<DefaultMember> defaultMemberList;
}
