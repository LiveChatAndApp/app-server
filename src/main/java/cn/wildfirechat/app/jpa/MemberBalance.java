package cn.wildfirechat.app.jpa;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "t_member_balance")
public class MemberBalance {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "_user_id")
    private Long userId;
    @Column(name = "_currency")
    private String currency;
    @Column(name = "_balance")
    private BigDecimal balance;
    @Column(name = "_freeze")
    private BigDecimal freeze;
    @Column(name = "_create_time")
    private Date createTime;
    @Column(name = "_update_time")
    private Date updateTime;

    public MemberBalance(Long userId, String currency, BigDecimal balance, BigDecimal freeze) {
        this.userId = userId;
        this.currency = currency;
        this.balance = balance;
        this.freeze = freeze;
    }

    public MemberBalance() {

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

    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFreeze() {
        return freeze;
    }
    public void setFreeze(BigDecimal freeze) {
        this.freeze = freeze;
    }
}
