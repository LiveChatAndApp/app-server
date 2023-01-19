package cn.wildfirechat.app.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAssertVO implements Serializable {
    private String currency;// 币种
    private String balance;// 馀额
    private String freeze;// 冻结金额
    private Boolean canRecharge;// 可充值
    private Boolean canWithdraw;// 可提现
}
