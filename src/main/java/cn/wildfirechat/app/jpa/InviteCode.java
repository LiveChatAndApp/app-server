package cn.wildfirechat.app.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
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
@Table(name = "t_invite_code")
public class InviteCode {

    @Id
    @Column(length = 19)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;// 流水号ID
    @Column(name = "_invite_code")
    private String inviteCode; // 邀请码
    @Column(name = "_friends_default_type")
    private Integer friendsDefaultType; // 预设好友模式 1: 所有, 2: 轮询[InviteCodeDefaultFriendTypeEnum]
    @Column(name = "_status")
    private Integer status; // 状态 0: 删除, 1: 使用中, 2:停用 [InviteCodeStatusEnum]
    @Column(name = "_note")
    private String note; // 后台备注
    @Column(name = "_create_time")
    private Date createTime;// 创建时间
    @Column(name = "_update_time")
    private Date updateTime;// 最后修改时间
    @Column(name = "_creator")
    private String creator; // 创建者
    @Column(name = "_updater")
    private String updater; // 修改者

//    @OneToMany(fetch = FetchType.LAZY, targetEntity = DefaultMember.class, mappedBy = "inviteCodeId")
//    private List<DefaultMember> defaultMemberList;
}
