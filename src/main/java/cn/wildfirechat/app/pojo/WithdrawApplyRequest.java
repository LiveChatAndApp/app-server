package cn.wildfirechat.app.pojo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
public class WithdrawApplyRequest {
    private String currency;// 币种
    private Integer channel;// 提现渠道 1:银行卡,2:游戏平台
    private Long paymentMethodId;// 收款方式ID
    private BigDecimal amount;// 提现金额
    @ToString.Exclude
    private String tradePwd;// 支付密码

}
