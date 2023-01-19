package cn.wildfirechat.app.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ChatRoomVO {
    private Long id;// 流水号ID

    private String cid; // 聊天室ID

    private String name; // 聊天室名称(聊天室信息)

    private Integer sort; // 排序

    private String image; // 图挡路径

    private Integer status; // 状态  0:停用,1:启用 [StatusBasicEnum]

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date createTime;// 创建时间

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date updateTime;// 最后修改时间

    private Integer chatStatus; // 发言状态 0:停用,1:启用 [StatusBasicEnum]

    private String desc;//聊天室详情描述

    private String extra;//附加信息

}
