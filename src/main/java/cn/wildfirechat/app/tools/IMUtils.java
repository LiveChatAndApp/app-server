package cn.wildfirechat.app.tools;

import cn.wildfirechat.app.enums.MessageChatEnum;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.Conversation;
import cn.wildfirechat.pojos.MessagePayload;
import cn.wildfirechat.pojos.SendMessageResult;
import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.sdk.MessageAdmin;
import cn.wildfirechat.sdk.model.IMResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IMUtils {
    private static final Logger LOG = LoggerFactory.getLogger(IMUtils.class);

    /**
     * 传送系统讯息
     * @param
     * @return void
     */
    public static void sendSystemMessage(String fromUser, String toUser, MessageChatEnum messageChatEnum) {
        sendMessage(fromUser, toUser, "", ProtoConstants.ConversationType.ConversationType_Private, messageChatEnum);
    }

    /**
     * 传送用户讯息
     * @param
     * @return void
     */
    public static void sendTextMessage(String fromUser, String toUser, String text) {
        sendMessage(fromUser, toUser, text, ProtoConstants.ConversationType.ConversationType_Private,
                MessageChatEnum.PRIVATE);
    }

    /**
     * 传送讯息
     * @param
     * @return void
     */
    private static void sendMessage(String fromUser, String toUser, String text, int conversationType,
                                    MessageChatEnum messageChatEnum) {
        Conversation conversation = new Conversation();
        conversation.setTarget(toUser);
        conversation.setType(conversationType);
        MessagePayload payload = new MessagePayload();
        payload.setType(messageChatEnum.getValue());
        payload.setSearchableContent(text);

        try {
            IMResult<SendMessageResult> resultSendMessage = MessageAdmin.sendMessage(fromUser, conversation, payload);
            if (resultSendMessage != null && resultSendMessage.getErrorCode() == ErrorCode.ERROR_CODE_SUCCESS) {
                LOG.info("send message success");
            } else {
                LOG.error("send message error {}",
                        resultSendMessage != null ? resultSendMessage.getErrorCode().code : "unknown");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("send message error {}", e.getLocalizedMessage());
        }
    }
}
