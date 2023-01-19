package cn.wildfirechat.app.pojo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RechargeConfirmRequest {
    public Long id; //订单ID
//    private String orderCode; //订单编号
    private MultipartFile payImageFile; //付款截图


}
