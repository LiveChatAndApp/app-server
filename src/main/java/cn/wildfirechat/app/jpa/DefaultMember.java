package cn.wildfirechat.app.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Date;

/**
 * Persistant Object 邀请码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_default_member")
public class DefaultMember {

	@Id
	@Column(length = 19)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;// 流水号ID

	@Column(name = "_default_type")
	private Integer defaultType; // 预设类型 1: 好友, 2: 群 [DefaultMemberDefaultTypeEnum]

//	@Column(name = "_member_id")
//	private Long memberId; // 预设好友ID

	@OneToOne()
	@JoinColumn(name = "_member_id")
	private Member member; // 预设好友

	@OneToOne
	@JoinColumn(name = "_group_id")
	private Group group; // 预设群ID

//	@OneToOne()
//	@JoinColumn(name = "_group_id")
//	private Group group; // 预设群ID

	@Column(name = "_welcome_text")
	private String welcomeText; // 欢迎词

	@Column(name = "_type")
	private Integer type;// 类型 1:所有新注册用户, 2:使用邀请码注册用户 [DefaultMemberTypeEnum]

	@Column(name = "_invite_code_id")
	private Long inviteCodeId;// 邀请码ID, 0为全部新加入者

	@Column(name = "_create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;// 创建时间

	@Column(name = "_update_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;// 最后修改时间

	@Column(name = "_creator")
	private String creator; // 创建者

	@Column(name = "_updater")
	private String updater; // 修改者
}
