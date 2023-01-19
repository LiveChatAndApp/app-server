package cn.wildfirechat.app;

public class RestResult {
    public enum  RestCode {

        SUCCESS(0000, "success"),

        ERROR_INVALID_MOBILE(1000, "无效的电话号码"),
        ERROR_SEND_SMS_OVER_FREQUENCY(1003, "请求验证码太频繁"),
        ERROR_SERVER_ERROR(1004, "服务器异常"),
        ERROR_CODE_EXPIRED(1005, "验证码已过期"),
        ERROR_CODE_INCORRECT(1006, "验证码或密码错误"),
        ERROR_SERVER_CONFIG_ERROR(1007, "服务器配置错误"),
        ERROR_SESSION_EXPIRED(1008, "会话不存在或已过期"),
        ERROR_SESSION_NOT_VERIFIED(1009, "会话没有验证"),
        ERROR_SESSION_NOT_SCANED(1010, "会话没有被扫码"),
        ERROR_SERVER_NOT_IMPLEMENT(1011, "功能没有实现"),
        ERROR_GROUP_ANNOUNCEMENT_NOT_EXIST(1012, "群公告不存在"),
        ERROR_NOT_LOGIN(1013, "没有登录"),
        ERROR_NO_RIGHT(1014, "没有权限"),
        ERROR_INVALID_PARAMETER(1015, "无效参数, %s"),
        ERROR_NOT_EXIST(1016, "对象不存在"),
        ERROR_USER_NAME_ALREADY_EXIST(1017, "用户名已经存在"),
        ERROR_SESSION_CANCELED(1018, "会话已经取消"),
        ERROR_PASSWORD_INCORRECT(1019, "密码错误"),
        ERROR_FAILURE_TOO_MUCH_TIMES(1020, "密码错误次数太多，请等5分钟再试试"),
        ERROR_IM_USER_NOT_EXIST(1021, "IM服务用户不存在, 请恰技术补户用注册"),
        ERROR_INVITE_CODE_INCORRECT(1022, "邀请码未输入或输入错误"),
        ERROR_MEMBER_BALANCE_INSUFFICIENT(1023, "会员馀额不足"),
        ERROR_WITHDRAW_CHANNEL_NOT_EXIST(1024, "提现渠道不存在"),
        ERROR_CURRENCY_NOT_SUPPORT(1025, "币种不支持"),
        ERROR_METHOD_NOT_SUPPORT(1026, "充值方式不支持"),
        ERROR_MEMBER_PROHIBIT_CODE_LOGIN(1027, "用户已设置密码,禁止已验证码流程登入"),
        ERROR_MEMBER_NOT_EXIST(1028,"会员不存在"),
        ERROR_MOBILE_MEMBER_EXIST(1029,"该手机号用户已存在"),
        ERROR_MOBILE_PASSWORD_WRONG(1030,"手机号或密码错误"),
        ERROR_WITHDRAW_INFO_MUST_NOT_BE_EMPTY(1031, "提现信息不得为空"),
        ERROR_WITHDRAW_PAYMENT_METHOD_NOT_EXIST(1032, "提现支付方式不存在"),
        ERROR_RELATE_FRIEND_IS_ALREADY(1033, "已为好友"),
        ERROR_MEMBER_LOGIN_DISABLE(1034, "目前为禁止登陆状态，请洽系统管理员"),
        ERROR_MEMBER_ADD_FRIEND_DISABLE(1035, "目前禁止添加好友，请洽系统管理员"),
        ERROR_FRIEND_TARGET_ERROR(1036, "新增好友不得为自己"),
        ERROR_FRIEND_VERIFY_TEXT_EMPTY(1037, "验证讯息不得为空"),
        ERROR_FRIEND_VERIFY_TEXT_ILLEGAL(1038, "验证讯息不符"),
        ERROR_RECHARGE_CHANNEL_NOT_SUPPORT(1039, "充值渠道不支持"),
        ERROR_RECHARGE_ORDER_NOT_EXIST_OR_WRONG_STATUS(1040, "充值订单不存在或状态错误"),
        ERROR_RELATE_FRIEND_IS_PENDING(1041, "已经送出邀请，等待对方回应"),
        ERROR_TRADER_PWD_NOT_THE_SAME(1042,"新密码与确认密码不一致"),
        ERROR_NEED_TRADE_PWD(1043,"需输入原交易密码"),
        ERROR_TRADE_PWD_NOT_CORRECT(1044,"交易密码验证错误"),
        ERROR_TRADE_PWD_REQUIRED(1045,"需輸入交易密码"),
        ERROR_UPLOAD_PORTRAIT(1046, "上传图挡失败"),


        UNKNOWN_ERROR(9999, "未知错误");

        public int code;
        public String msg;

        RestCode(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

    }
    private int code;
    private String message;
    private Object result;

    public static RestResult ok(Object object) {
        return new RestResult(RestCode.SUCCESS, object);
    }

    public static RestResult error(RestCode code) {
        return new RestResult(code, null);
    }

    public static RestResult error(RestCode code, String message) {
        return new RestResult(code, message, null);
    }

    public static RestResult result(RestCode code, Object object){
        return new RestResult(code, object);
    }

    public static RestResult result(int code, String message, Object object){
        RestResult r = new RestResult(RestCode.SUCCESS, object);
        r.code = code;
        r.message = message;
        return r;
    }

    private RestResult(RestCode code, Object result) {
        this.code = code.code;
        this.message = code.msg;
        this.result = result;
    }

    private RestResult(RestCode code, String message, Object result) {
        this.code = code.code;
        this.message = message;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
