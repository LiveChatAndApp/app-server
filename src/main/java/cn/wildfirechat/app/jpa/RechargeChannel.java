package cn.wildfirechat.app.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_recharge_channel")
public class RechargeChannel {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "_name")
    private String name;

    @Column(name = "_payment_method")
    private Integer paymentMethod;

    @Column(name = "_info")
    private String info;

    @Column(name = "_qrcode_image")
    private String qrCodeImage;

    @Column(name = "_status")
    private Integer status;

    @Column(name = "_memo")
    private String memo;

    @Column(name = "_create_time")
    private Date createTime;

    @Column(name = "_update_time")
    private Date updateTime;


}
