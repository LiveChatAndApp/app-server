package cn.wildfirechat.app.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object 充值渠道传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeChannelInfoDTO {

    private String realName;//真实姓名

    private String bankName;//银行名称

    private String bankAccount;//银行账号

    private String qrCodeImage;//QRCode图
}
