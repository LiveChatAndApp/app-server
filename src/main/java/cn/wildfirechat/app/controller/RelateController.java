package cn.wildfirechat.app.controller;

import cn.wildfirechat.app.RestResult;
import cn.wildfirechat.app.enums.MessageChatEnum;
import cn.wildfirechat.app.enums.RelateReplyEnum;
import cn.wildfirechat.app.enums.RelateVerifyEnum;
import cn.wildfirechat.app.jpa.Friend;
import cn.wildfirechat.app.jpa.FriendRepository;
import cn.wildfirechat.app.jpa.Member;
import cn.wildfirechat.app.jpa.MemberRepository;
import cn.wildfirechat.app.model.form.AddFriendRequestForm;
import cn.wildfirechat.app.model.form.RespondFriendInviteForm;
import cn.wildfirechat.app.model.vo.FriendRequestVO;
import cn.wildfirechat.app.tools.IMUtils;
import cn.wildfirechat.app.tools.ReflectionUtils;
import cn.wildfirechat.app.tools.ResponseConverterUtils;
import cn.wildfirechat.app.tools.StringUtil;
import cn.wildfirechat.sdk.RelationAdmin;
import cn.wildfirechat.sdk.model.IMResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static cn.wildfirechat.app.RestResult.RestCode.ERROR_SERVER_ERROR;

@Api(tags = "关系接口")
@RestController()
@RequestMapping("/relate")
public class RelateController {
	private static final Logger LOG = LoggerFactory.getLogger(RelateController.class);

	@Autowired
	private FriendRepository friendRepository;

	@Autowired
	private MemberRepository memberRepository;

	@ApiOperation(value = "查询好友邀请列表")
	@GetMapping("/friend/request")
	public RestResult friendRequest() {
		try {
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");
			String userId = (String) subject.getSession().getAttribute("userId");
			LOG.info("查询好友邀请列表 {}", memberId);
			List<Friend> friendList = friendRepository.findFriendRequest(memberId);
			List<FriendRequestVO> friendRequestVOList = friendList.stream().map(friend -> {
				LOG.info("FriendRequest: {}", friend);
				Long senderId = friend.getMemberSource().getId();
				if (friend.getMemberSource().getId().equals(friend.getRequestReceiver())) {
					senderId = friend.getMemberTargetId();
				}
				Member member = memberRepository.findById(senderId).orElse(null);
				FriendRequestVO friendRequestVO = new FriendRequestVO();
				ReflectionUtils.copyFields(friendRequestVO, member, ReflectionUtils.STRING_TRIM_TO_NULL);
				friendRequestVO.setAvatar(member.getAvatarUrl());
				friendRequestVO.setMobile(member.getPhone());
				friendRequestVO.setVerify(friend.getVerify().equals(RelateVerifyEnum.VERIFY.getValue()));
				friendRequestVO.setHelloText(friend.getHelloText());
				return friendRequestVO;
			}).collect(Collectors.toList());

			return RestResult.ok(friendRequestVOList);
		} catch (Exception e) {
			LOG.error("查询好友邀请列表 exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "发送好友邀请", httpMethod = "POST")
	@PostMapping("/friend/invite")
	public RestResult sendFriendInviteRequest(@RequestBody AddFriendRequestForm form) {
		try {
			LOG.info("发送好友邀请 Data:{}", form);
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");
			String userId = (String) subject.getSession().getAttribute("userId");

			if (form.isVerify() && StringUtil.isEmpty(form.getVerifyText())) {
				LOG.info("发送好友邀请 failed: {}", RestResult.RestCode.ERROR_FRIEND_VERIFY_TEXT_EMPTY.msg);
				return RestResult.error(RestResult.RestCode.ERROR_FRIEND_VERIFY_TEXT_EMPTY);
			}

			Member currentMember = memberRepository.findByUid(userId);
			Member member = memberRepository.findByUid(form.getUid());
			LOG.info("发送好友邀请[Member]: {}", member);
			if (member == null) {
				LOG.info("发送好友邀请 failed: {}", RestResult.RestCode.ERROR_MEMBER_NOT_EXIST.msg);
				return RestResult.error(RestResult.RestCode.ERROR_MEMBER_NOT_EXIST);
			} else if (member.getUid().equalsIgnoreCase(currentMember.getUid())) {
				LOG.info("发送好友邀请 failed: {}", RestResult.RestCode.ERROR_FRIEND_TARGET_ERROR.msg);
				return RestResult.error(RestResult.RestCode.ERROR_FRIEND_TARGET_ERROR);
			}
			if (currentMember.getAddFriendEnable()) {
				Optional<Friend> friendOptional = friendRepository.relateExist(memberId, member.getId());
				if (friendOptional.isPresent()) {
					if (friendOptional.get().getVerify().equals(RelateVerifyEnum.SUCCESS.getValue())) {
						LOG.info("发送好友邀请 failed: {}", RestResult.RestCode.ERROR_RELATE_FRIEND_IS_ALREADY.msg);
						return RestResult.error(RestResult.RestCode.ERROR_RELATE_FRIEND_IS_ALREADY);
					} else if (friendOptional.get().getVerify() < 2) {
						LOG.info("发送好友邀请 failed: {}", RestResult.RestCode.ERROR_RELATE_FRIEND_IS_PENDING.msg);
						return RestResult.error(RestResult.RestCode.ERROR_RELATE_FRIEND_IS_PENDING);
					}
				}

				IMResult<Void> voidIMResult = RelationAdmin.sendFriendRequest(userId, member.getUid(), "", false);

				Friend friend = friendRepository.relateExist(currentMember.getId(), member.getId()).orElse(null);
				if (friend == null) {
					friend = new Friend();
					friend.setMemberSource(currentMember);
					friend.setMemberTargetId(member.getId());
				}
				ReflectionUtils.copyFields(friend, form, ReflectionUtils.STRING_TRIM_TO_NULL);
				friend.setVerify(
						form.isVerify() ? RelateVerifyEnum.VERIFY.getValue() : RelateVerifyEnum.PENDING.getValue());
				friend.setRequestReceiver(member.getId());
				friendRepository.save(friend);
				return ResponseConverterUtils.converter(voidIMResult);
			} else {
				LOG.info("发送好友邀请 failed: {}", RestResult.RestCode.ERROR_MEMBER_ADD_FRIEND_DISABLE.msg);
				return RestResult.error(RestResult.RestCode.ERROR_MEMBER_ADD_FRIEND_DISABLE);
			}
		} catch (Exception e) {
			LOG.error("发送好友邀请 exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

	@ApiOperation(value = "回应好友邀请")
	@PostMapping("/friend/response")
	public RestResult friendResponse(@RequestBody RespondFriendInviteForm form) {
		try {
			Subject subject = SecurityUtils.getSubject();
			Long memberId = (Long) subject.getSession().getAttribute("memberId");
			String userId = (String) subject.getSession().getAttribute("userId");
			LOG.info("回应好友邀请 memberId: {}, form: {}", memberId, form);
			Member member = memberRepository.findByUid(form.getUid());
			if (member == null) {
				return RestResult.error(RestResult.RestCode.ERROR_MEMBER_NOT_EXIST);
			}
			Optional<Friend> friendOptional = friendRepository.relateExist(memberId, member.getId());
			if (friendOptional.isPresent()) {

				if(!ObjectUtils.isEmpty(form.getReply())
						&& form.getReply().equals(RelateReplyEnum.REJECT.getValue())){
					RelationAdmin.setUserFriend(userId, member.getUid(), false, "");
					return RestResult.ok(null);
				}

				boolean isAdd = false;
				Friend friend = friendOptional.get();
				if (friend.getVerify().equals(RelateVerifyEnum.PENDING.getValue())) {
					isAdd = true;
				} else if (friend.getVerify().equals(RelateVerifyEnum.VERIFY.getValue())) {
					if (form.getVerifyText().equals(friend.getVerifyText())) {
						isAdd = true;
					} else {
						return RestResult.error(RestResult.RestCode.ERROR_FRIEND_VERIFY_TEXT_ILLEGAL);
					}
				}

				if (isAdd) {
					RelationAdmin.setUserFriend(userId, member.getUid(), true, "");
					IMUtils.sendSystemMessage(member.getUid(), userId, MessageChatEnum.SYSTEM_FRIEND_ALREADY_MESSAGE);
					if (StringUtil.isNotEmpty(friend.getHelloText())) {
						IMUtils.sendTextMessage(member.getUid(), userId, friend.getHelloText());
					}
				}


			}

			return RestResult.ok(null);
		} catch (Exception e) {
			LOG.error("回应好友邀请 exception", e);
			return RestResult.error(ERROR_SERVER_ERROR);
		}
	}

}
