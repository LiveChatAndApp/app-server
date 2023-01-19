package cn.wildfirechat.app.jpa;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "t_member_balance_log")
public class MemberBalanceLog {
    public static final String USER_WITHDRAW_APPLY_FREEZE = "用户申请提币: {amount}, 冻结增加: {freeze}";

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;// ID
    @Column(name = "_user_id")
    private Long userId;// 用户ID
    @Column(name = "_currency")
    private String currency;// 币种
    @Column(name = "_type")
    private Integer type;// 类型 1:手动充值,2:手动提取
    @Column(name = "_amount")
    private BigDecimal amount;// 交易金额
    @Column(name = "_before_balance")
    private BigDecimal beforeBalance;// 交易前馀额
    @Column(name = "_after_balance")
    private BigDecimal afterBalance;// 交易后馀额
    @Column(name = "_before_freeze")
    private BigDecimal beforeFreeze;// 交易前冻结金额
    @Column(name = "_after_freeze")
    private BigDecimal afterFreeze;// 交易后冻结金额
    @Column(name = "_memo")
    private String memo;// 备注
    @Column(name = "_create_time")
    private Date createTime;// 创建时间

    public MemberBalanceLog(Long userId, String currency, Integer type, BigDecimal amount, BigDecimal beforeBalance, BigDecimal afterBalance, BigDecimal beforeFreeze, BigDecimal afterFreeze, String memo) {
        this.userId = userId;
        this.currency = currency;
        this.type = type;
        this.amount = amount;
        this.beforeBalance = beforeBalance;
        this.afterBalance = afterBalance;
        this.beforeFreeze = beforeFreeze;
        this.afterFreeze = afterFreeze;
        this.memo = memo;
        this.createTime = new Date();
    }

    public MemberBalanceLog() {

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

    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBeforeBalance() {
        return beforeBalance;
    }
    public void setBeforeBalance(BigDecimal beforeBalance) {
        this.beforeBalance = beforeBalance;
    }

    public BigDecimal getAfterBalance() {
        return afterBalance;
    }
    public void setAfterBalance(BigDecimal afterBalance) {
        this.afterBalance = afterBalance;
    }

    public BigDecimal getBeforeFreeze() {
        return beforeFreeze;
    }
    public void setBeforeFreeze(BigDecimal beforeFreeze) {
        this.beforeFreeze = beforeFreeze;
    }

    public BigDecimal getAfterFreeze() {
        return afterFreeze;
    }
    public void setAfterFreeze(BigDecimal afterFreeze) {
        this.afterFreeze = afterFreeze;
    }

    public String getMemo() {
        return memo;
    }
    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
