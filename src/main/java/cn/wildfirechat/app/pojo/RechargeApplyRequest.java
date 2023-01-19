package cn.wildfirechat.app.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeApplyRequest {
    private String currency;// 币种
    private Integer method;// 充值方式 1:线下支付,2:微信,3:支付宝
    private BigDecimal amount;// 提现金额
    private Long channelId;// 充值渠道ID

}
