package cn.wildfirechat.app.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Persistant Object 群组成员
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_group_member")
public class GroupMember {

    @Id
    @Column(length = 19)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;// ID

    @Column(name = "_gid")
    private String gid; // 群组ID

    @OneToOne()
    @JoinColumn(name = "_member_id")
    private Member member; // 成员

    @OneToOne()
    @JoinColumn(name = "_group_id")
    private Group group; // 群组

    @Column(name = "_member_type")
    private Integer memberType; // 成员类型 1: 成员, 2: 管理员, 3: 群主

    @Column(name = "_verify")
    private Integer verify; //验证 0: 待验证, 1: 成功, 2: 失败

    @Column(name = "_verify_text")
    private String verifyText;// 验证消息

    @CreationTimestamp
    @Column(name = "_create_time")
    private Date createTime;// 建立时间

    @UpdateTimestamp
    @Column(name = "_update_time")
    private Date updateTime;// 验证消息
}