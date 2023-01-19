package cn.wildfirechat.app.pojo;

public class WithdrawPaymentMethodAddRequest {
    private String name;// 银行卡名称
    private Integer channel;// 收款方式类型
    private String info;// 收款信息

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getChannel() {
        return channel;
    }
    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
}
