package cn.wildfirechat.app.util;

import cn.wildfirechat.app.enums.MessageChatEnum;
import cn.wildfirechat.app.enums.MessageTypeEnum;
import cn.wildfirechat.app.model.ws.SocketConfig;
import cn.wildfirechat.app.model.ws.WebSocketResult;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/websocket/{uid}")
@Component
@Slf4j
public class WebsocketUtils {
    private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
    private static Map<String, Session> sessionMap = new HashMap<>();

    private static CopyOnWriteArraySet<WebsocketUtils> clients = new CopyOnWriteArraySet<>();

    private Session session;

    private String uid;

    private final static String IMAGE_PATH = "/Users/paige.chen/Desktop";
    private final static String IMAGE_EXTENSION = ".jpg";

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid) throws Exception {
        this.session = session;
        this.uid = uid;

        sessionMap.put(session.getId(), session);

        clients.add(this);
        log.info("有新用户[{}]加入，当前人数为: [{}]", uid, clients.size());

        String msg = String.format("已成功连接，其频道号为: [%s]，当前在线人数为: [%s]", session.getId(), clients.size());
        WebSocketResult result = WebSocketResult.builder()
                .messageType(MessageTypeEnum.TEXT.getValue())
                .msg(msg)
                .uid(uid)
                .build();
        this.session.getAsyncRemote().sendText(objectMapper.writeValueAsString(result));
    }

    @OnClose
    public void onClose(){
        clients.remove(this);
        log.info("有用户断开连接,当前人数为：{}", clients.size());
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("uid") String uid) {
        log.info("用户: [{}]发来的消息：{}", uid, message);
        try {
            SocketConfig socketConfig = objectMapper.readValue(message, SocketConfig.class);
            WebSocketResult wsObject = WebSocketResult.builder()
                    .messageType(socketConfig.getMessageType())
                    .uid(uid+": ")
                    .build();
            String text;
            if (socketConfig.getMessageType() == 2) {
                String[] imageBase64s = socketConfig.getMsg().split(",");
                String imageBase64 = imageBase64s.length > 1 ? imageBase64s[1] : imageBase64s[0];
                byte[] decodedImg = Base64.getMimeDecoder().decode(imageBase64.getBytes(StandardCharsets.UTF_8));
                String filename = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + IMAGE_EXTENSION;
                Path destinationFile = Paths.get(IMAGE_PATH, filename);
                Files.write(destinationFile, decodedImg);
                text = "file://"+IMAGE_PATH + filename;
            } else {
                text = socketConfig.getMsg();
            }

            if (MessageChatEnum.PRIVATE == MessageChatEnum.parse(socketConfig.getChatType())) {
                socketConfig.setFromUser(session.getId());
                Session fromSession = sessionMap.get(socketConfig.getFromUser());
                Session toSession = sessionMap.get(socketConfig.getToUser());
                if (toSession != null) {
                    // 接受者存在，发送以下消息给接受者和发送者
                    wsObject.setMsg(text);
                    String json = objectMapper.writeValueAsString(wsObject);
                    fromSession.getAsyncRemote().sendText(json);
                    toSession.getAsyncRemote().sendText(json);
                } else {
                    // 发送者不存在，发送以下消息给发送者
                    text = "频道号不存在或对方不在线";
                    fromSession.getAsyncRemote().sendText(text);
                }
            } else {
                // 群聊
                wsObject.setMsg(text);
                broadcast(objectMapper.writeValueAsString(wsObject));
            }
        } catch (Exception e) {
            log.error("发送消息出错");
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error){
        log.error("出现错误 sessionId: {}, session: {}", session.getId(), session);
        error.printStackTrace();
    }

    /**
     * 自定义群发消息
     * @param message
     */
    public void broadcast(String message){
        for (WebsocketUtils websocket : clients){
            //异步发送消息
            websocket.session.getAsyncRemote().sendText(message);
        }
    }
}