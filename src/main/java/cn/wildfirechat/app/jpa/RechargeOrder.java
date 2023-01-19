package cn.wildfirechat.app.jpa;

import cn.wildfirechat.app.tools.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.utility.RandomString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "t_recharge_order")
public class RechargeOrder {
    public static final String CODE_PREFIX = "PAY";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "_order_code")
    private String orderCode;

    @Column(name = "_user_id")
    private Long userId;

    @Column(name = "_method")
    private Integer method;

    @Column(name = "_amount")
    private BigDecimal amount;

    @Column(name = "_channel_id")
    private Long channelId;

    @Column(name = "_pay_image")
    private String payImage;

    @Column(name = "_currency")
    private String currency;

    @Column(name = "_status")
    private Integer status;

    @Column(name = "_create_time")
    private Date createTime;

    @Column(name = "_complete_time")
    private Date completeTime;

    @Column(name = "_updater_id")
    private Long updaterId;

    @Column(name = "_updater_role")
    private Integer updaterRole;

    @Column(name = "_update_time")
    private Date updateTime;

    public RechargeOrder(String orderCode, Long userId, Integer method, BigDecimal amount, String currency, Long channelId, Integer status, Integer updaterRole) {
        this.orderCode = orderCode;
        this.userId = userId;
        this.method = method;
        this.amount = amount;
        this.currency = currency;
        this.channelId = channelId;
        this.status = status;
        this.createTime = new Date();
        this.updaterRole = updaterRole;
    }

    public RechargeOrder() {

    }

    public static String randomOrderCode() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<5;i++) {
            builder.append(random.nextInt(10));
        }

        String orderCode = String.format("%s%s%s", CODE_PREFIX, DateUtils.format(new Date(), DateUtils.YMDHMSSS), builder);
        return orderCode;
    }

}
