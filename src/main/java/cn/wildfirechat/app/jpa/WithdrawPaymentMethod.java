package cn.wildfirechat.app.jpa;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_withdraw_payment_method")
public class WithdrawPaymentMethod {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "_user_id")
    private Long userId;

    @Column(name = "_name")
    private String name;

    @Column(name = "_payment_method")
    private Integer paymentMethod;

    @Column(name = "_info")
    private String info;

    @Column(name = "_image")
    private String image;

    @Column(name = "_create_time")
    private Date createTime;

    @Column(name = "_update_time")
    private Date updateTime;

    public WithdrawPaymentMethod(Long userId, String name, Integer paymentMethod, String info) {
        this.userId = userId;
        this.name = name;
        this.paymentMethod = paymentMethod;
        this.info = info;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    public WithdrawPaymentMethod() {

    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
