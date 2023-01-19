package cn.wildfirechat.app;

import cn.wildfirechat.app.jpa.MemberRepository;
import cn.wildfirechat.app.tools.ThreadPoolUtils;
import cn.wildfirechat.pojos.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/*
IM对应事件发生时，会回调到配置地址。需要注意IM服务单线程进行回调，如果接收方处理太慢会导致推送线程被阻塞，导致延迟发生，甚至导致IM系统异常。
建议异步处理快速返回，这里收到后转到异步线程处理，并且立即返回。另外两个服务器的ping值不能太大。
 */
@RestController()
public class IMCallbackController {
	private static final String OK = "ok";

	private ObjectMapper objectMapper = new ObjectMapper();

	private static final Logger LOG = LoggerFactory.getLogger(IMCallbackController.class);

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ThreadPoolUtils threadPoolUtils;

	/*
	 * 用户在线状态回调
	 */
	@PostMapping(value = "/im_event/user/online")
	public Object onUserOnlineEvent(@RequestBody UserOnlineStatus event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onUserOnlineEvent（用户在线状态回调） json: {}", json);
		return OK;
	}

	/*
	 * 用户关系变更回调
	 */
	@PostMapping(value = "/im_event/user/relation")
	public Object onUserRelationUpdated(@RequestBody RelationUpdateEvent event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onUserRelationUpdated（用户关系变更回调） json: {}", json);
		return OK;
	}

	/*
	 * 用户信息更新回调
	 */
	@PostMapping(value = "/im_event/user/info")
	public Object onUserInfoUpdated(@RequestBody InputOutputUserInfo event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onUserInfoUpdated（用户信息更新回调） json: {}", json);
		return OK;
	}

	/*
	 * 发送消息回调
	 */
	@PostMapping(value = "/im_event/message")
	public Object onMessage(@RequestBody OutputMessageData event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onMessage（发送消息回调） json: {}", json);
		return OK;
	}

	/*
	 * 物联网消息回调
	 */
	@PostMapping(value = "/im_event/things/message")
	public Object onThingsMessage(@RequestBody OutputMessageData event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onThingsMessage（物联网消息回调） json: {}", json);
		return OK;
	}

	/*
	 * 群组信息更新回调
	 */
	@PostMapping(value = "/im_event/group/info")
	public Object onGroupInfoUpdated(@RequestBody GroupUpdateEvent event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onGroupInfoUpdated（群组信息更新回调） json: {}", json);
		return OK;
	}

	/*
	 * 群组成员更新回调
	 */
	@PostMapping(value = "/im_event/group/member")
	public Object onGroupMemberUpdated(@RequestBody GroupMemberUpdateEvent event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onGroupMemberUpdated（群组成员更新回调） json: {}", json);
		return OK;
	}

	/*
	 * 频道信息更新回调
	 */
	@PostMapping(value = "/im_event/channel/info")
	public Object onChannelInfoUpdated(@RequestBody ChannelUpdateEvent event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onChannelInfoUpdated（频道信息更新回调） json: {}", json);
		return OK;
	}

	/*
	 * 聊天室信息更新回调
	 */
	@PostMapping(value = "/im_event/chatroom/info")
	public Object onChatroomInfoUpdated(@RequestBody ChatroomUpdateEvent event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onChatroomInfoUpdated（聊天室信息更新回调） json: {}", json);
		return OK;
	}

	/*
	 * 聊天室成员更新回调
	 */
	@PostMapping(value = "/im_event/chatroom/member")
	public Object onChatroomMemberUpdated(@RequestBody ChatroomMemberUpdateEvent event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onChatroomMemberUpdated（聊天室成员更新回调） json: {}", json);
		return OK;
	}

	/*
	 * 消息审查示例。
	 *
	 * 如果允许发送，返回状态码为200，内容为空；如果替换内容发送，返回状态码200，内容为替换过的payload内容。如果不允许发送，返回状态码403。
	 */
	@PostMapping(value = "/message/censor")
	public Object censorMessage(@RequestBody OutputMessageData event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("censorMessage（消息审查示例） json: {}", json);
		if (event.getPayload().getSearchableContent() != null
				&& event.getPayload().getSearchableContent().contains("testkongbufenzi")) {
			throw new ForbiddenException();
		}
		if (event.getPayload().getSearchableContent() != null
				&& event.getPayload().getSearchableContent().contains("testzhaopian")) {
			event.getPayload()
					.setSearchableContent(event.getPayload().getSearchableContent().replace("zhaopian", "照片"));
			return new Gson().toJson(event.getPayload());
		}
		return "";
	}

	@PostMapping(value = "/im_event/conference/create")
	public Object onConferenceCreated(@RequestBody ConferenceCreateEvent event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onConferenceCreated json: {}", json);
		return OK;
	}

	@PostMapping(value = "/im_event/conference/destroy")
	public Object onConferenceDestroyed(@RequestBody ConferenceDestroyEvent event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onConferenceDestroyed json: {}", json);
		return OK;
	}

	@PostMapping(value = "/im_event/conference/member_join")
	public Object onConferenceMemberJoined(@RequestBody ConferenceJoinEvent event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onConferenceMemberJoined json: {}", json);
		return OK;
	}

	@PostMapping(value = "/im_event/conference/member_leave")
	public Object onConferenceMemberLeaved(@RequestBody ConferenceLeaveEvent event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onConferenceMemberLeaved json: {}", json);
		return OK;
	}

	@PostMapping(value = "/im_event/conference/member_publish")
	public Object onConferenceMemberPublished(@RequestBody ConferencePublishEvent event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onConferenceMemberPublished json: {}", json);
		return OK;
	}

	@PostMapping(value = "/im_event/conference/member_unpublish")
	public Object onConferenceMemberUnpublished(@RequestBody ConferenceUnpublishEvent event) throws Exception {
		String json = objectMapper.writeValueAsString(event);
		LOG.info("onConferenceMemberUnpublished json: {}", json);
		return OK;
	}
}
