package cn.wildfirechat.app.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * Persistant Object 聊天室
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_chatroom")
public class ChatRoom {
    @Id
    @Column(length = 19)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;// 流水号ID

    @Column(name = "_cid")
    private String cid; // 聊天室ID

    @Column(name = "_name")
    private String name; // 聊天室名称(聊天室信息)

    @Column(name = "_sort")
    private Integer sort; // 排序

    @Column(name = "_image")
    private String image; // 图挡路径

    @Column(name = "_status")
    private Integer status; // 状态  0:停用,1:启用 [StatusBasicEnum]

    @Column(name = "_create_time")
    private Date createTime;// 创建时间

    @Column(name = "_update_time")
    private Date updateTime;// 最后修改时间

    @Column(name = "_chat_status")
    private Integer chatStatus; // 发言状态 0:停用,1:启用 [StatusBasicEnum]

    @Column(name = "_desc")
    private String desc;//聊天室详情描述

    @Column(name = "_extra")
    private String extra;//附加信息

}
