package cn.wildfirechat.app.model.vo;

import cn.wildfirechat.app.pojo.RechargeChannelInfoDTO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Persistant Object 充值渠道持久对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("充值渠道")
public class RechargeChannelVO {
    private Long id;// ID
    private String name;// 充值渠道名称
    private Integer paymentMethod;// 收款方式 1:银行卡,2:微信,3:支付宝
    private RechargeChannelInfoDTO info;// 收款方式资讯
//    private String info;// 收款方式资讯
//    private String qrCodeImage;// QRCode路径
    private Integer status;// 状态 1:待审查,2:已完成,3:已拒绝
    private String memo;// 备注
    private Date createTime;// 创建时间
    private Date updateTime;// 最后修改时间
}
