package cn.wildfirechat.app.jpa;

import cn.wildfirechat.app.tools.DateUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

@Entity
@Table(name = "t_withdraw_order")
public class WithdrawOrder {
    public static final String CODE_PREFIX = "W";

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "_order_code")
    private String orderCode;

    @Column(name = "_user_id")
    private Long userId;

    @Column(name = "_currency")
    private String currency;

    @Column(name = "_channel")
    private Integer channel;

    @Column(name = "_account_info")
    private String accountInfo;

    @Column(name = "_amount")
    private BigDecimal amount;

    @Column(name = "_status")
    private Integer status;

    @Column(name = "_create_time")
    private Date createTime;

    @Column(name = "_updater_role")
    private Integer updaterRole;

    public WithdrawOrder(String orderCode, Long userId, String currency, Integer channel, String accountInfo, BigDecimal amount, Integer status, Integer updaterRole) {
        this.orderCode = orderCode;
        this.userId = userId;
        this.currency = currency;
        this.channel = channel;
        this.accountInfo = accountInfo;
        this.amount = amount;
        this.status = status;
        this.createTime = new Date();
        this.updaterRole = updaterRole;
    }

    public WithdrawOrder() {

    }

    public static String randomOrderCode() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<8;i++) {
            builder.append(random.nextInt(10));
        }

        String orderCode = String.format("%s%s%s", CODE_PREFIX, DateUtils.format(new Date(), DateUtils.YMDHMSSS), builder);
        return orderCode;
    }

    public String getOrderCode() {
        return orderCode;
    }
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getChannel() {
        return channel;
    }
    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public String getAccountInfo() {
        return accountInfo;
    }
    public void setAccountInfo(String accountInfo) {
        this.accountInfo = accountInfo;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getUpdaterRole() {
        return updaterRole;
    }
    public void setUpdaterRole(Integer updaterRole) {
        this.updaterRole = updaterRole;
    }
}
