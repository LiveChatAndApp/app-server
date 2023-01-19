package cn.wildfirechat.app.model.vo;

import cn.wildfirechat.app.jpa.RechargeChannel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneralOrderVO implements Serializable {

    private Long id; //订单ID
    private Integer type; //类型 1:手动充值,2:手动提取
    private RechargeChannelVO rechargeChannel; //支付渠道
    private String orderCode; //订单编号
    private String amount; //金额
    //充值[[RechargeOrderStatusEnum]]状态 0:訂單成立, 1:待审查,2:已完成,3:已拒绝
    //提现[[WithdrawOrderStatusEnum]]状态 1:待审核,2:已完成,3:已拒绝,4:用户取消
    private Integer status;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date createTime;//创建时间
}
