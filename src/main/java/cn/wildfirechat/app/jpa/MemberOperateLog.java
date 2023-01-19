package cn.wildfirechat.app.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_member_operate_log")
public class MemberOperateLog {


    @Id
    @Column(length = 19)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;//流水号

    @Column(name = "_uid")
    private String uid;//用戶UID编号
    @Column(name = "_memo")
    private String memo;//备注
    @Column(name = "_auth_id")
    private Long authId;//请求方法
    @Column(name = "_create_time")
    private Date createTime;//创建时间
    @Column(name = "_creator_level")
    private Integer creatorLevel;//创建者Level
    @Column(name = "_creator")
    private String creator;//创建者帐号
    @Column(name = "_creator_ip")
    private String creatorIp;//创建者IP
    @Column(name = "_creator_location")
    private String creatorLocation;//创建者位置
}
