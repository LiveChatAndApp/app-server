package cn.wildfirechat.app.model.ws;

import lombok.Data;

@Data
public class SocketConfig {
    private Integer chatType;// 聊天类型 0:群聊  1:私聊
    private String fromUser;// 发送者
    private String toUser;// 接受者
    private String msg;// 消息
    private Integer messageType;// 消息类型 1:文本, 2:图片
}