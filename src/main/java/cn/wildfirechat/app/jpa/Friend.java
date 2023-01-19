package cn.wildfirechat.app.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Persistant Object 朋友持久对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_friend", uniqueConstraints = @UniqueConstraint(columnNames = {"_member_source_id",
		"_member_target_id"}))
public class Friend implements Serializable {

	@Id
	@Column(length = 19)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;// 朋友流水号

	@OneToOne()
	@JoinColumn(name = "_member_source_id")
	private Member memberSource;// 邀请者

	@Column(name = "_member_target_id")
	private Long memberTargetId; // 接受者ID

	@Column(name = "_verify")
	private Integer verify; // 验证 0: 待同意, 1: 需要验证讯息, 2: 成功, 3: 失败 [RelateVerifyEnum]

	@Column(name = "_verify_text")
	private String verifyText; // 验证消息

	@Column(name = "_request_receiver")
	private Long requestReceiver; // 好友邀请接收者ID

	@Column(name = "_hello_text")
	private String helloText; // 打招呼讯息

	@Column(name = "_create_time")
	@CreationTimestamp
	private Date createTime;// 创建时间

	@UpdateTimestamp
	@Column(name = "_update_time")
	private Date updateTime;// 最后修改时间

	@Column(name = "_add_time")
	private Date addTime;// 新增时间
}
